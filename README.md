# MultipleBackstackApplier

## Intro
As you know [Jetpack Navigation Component](https://developer.android.com/guide/navigation) has default multiple backstack since 2.4.0-alpha01. 
[How to use Sample](https://github.com/mengdd/bottom-navigation-samples).


Howewer it does not work properly. Some people are struggling there in comments:

[Navigation: Multiple back stacks](https://medium.com/androiddevelopers/navigation-multiple-back-stacks-6c67ba41952f)

[Android navigation with multiple back stacks](https://medium.com/@danmeng_44632/android-navigation-with-multiple-back-stacks-f8585e497a03)

[Navigation: Multiple back stacks - MAD Skills](https://www.youtube.com/watch?v=Covu3fPA1nQ)


## Alternatives
Despite there is well-known third-party libraries: 
[Enro](https://github.com/isaac-udy/Enro),
[Simple-stack](https://github.com/Zhuinden/simple-stack).
Also you may use another navigation library.


## Dive into the problem
Here you can check in official doc how to implement multiple backstack. [Integration with the bottom nav bar](https://developer.android.com/jetpack/compose/navigation#bottom-nav)

Google devs said here [Multiple back stacks: A deep dive into what actually went into this feature](https://medium.com/androiddevelopers/multiple-back-stacks-b714d974f134) that:

"The Navigation Component was built from the beginning as a generic runtime that knows nothing about Views, Fragments, Composables, or any other type of screen or ‘destination’ you might implement within your activity."

But we have contrary statements in code. They tied and bond `navController`(keeps track of the current position within the navigation graph. It orchestrates swapping destination content in the NavHost as you move through a navigation graph) 
with UI framework. 

Exactly in that spot we have an issue `NavigationBarView.setupWithNavController(navController)`. View controlls destination selection and we always have coupling to UI framework.

```
    @JvmStatic
    public fun setupWithNavController(
        navigationBarView: NavigationBarView,
        navController: NavController
    ) {
       // View controlls destination selection
        navigationBarView.setOnItemSelectedListener { item ->
            ...
        }
        // Reference to UI framework
        val weakReference = WeakReference(navigationBarView)
        navController.addOnDestinationChangedListener(
            object : NavController.OnDestinationChangedListener {
                override fun onDestinationChanged(
                    controller: NavController,
                    destination: NavDestination,
                    arguments: Bundle?
                ) {
                    val view = weakReference.get()
                    if (view == null) {
                        navController.removeOnDestinationChangedListener(this)
                        return
                    }
                    view.menu.forEach { item ->
                        if (destination.matchDestination(item.itemId)) {
                            item.isChecked = true
                        }
                    }
                }
            })
    }
```


## Implementation
I decided to face the issue and dealt with it.
You needn't reinvent the wheel. My solution based on Jetpack Navigation Component. I tear apart relations between UI framework and NavController.
All you need to do is:
1. Check source code of MultipleBackstackGraphItem and build navigation graph. Abide to all comments.
```
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
xmlns:app="http://schemas.android.com/apk/res-auto"
android:id="@+id/camera_nav_graph" // this is your navigationGraphId
app:startDestination="@id/cameraGraphFragment">  // this is your startDestinationId
...
...
</navigation>
 
interface MultipleBackstackGraphItem {
    // Id of navigation graph for multiple backstack.
    // Graph must be included in "main|desirable|bottom" navigation graph.
    val navigationGraphId: Int
    // You could use this id to check when you are on the start destination.
    val startDestinationId: Int
}

```
2. Implement MultipleBackstackGraphItem. For example:
```
enum class BottomNavigationItem(
    override val navigationGraphId: Int,
    override val startDestinationId: Int
) : MultipleBackstackGraphItem {
    Left(R.id.left_nav_graph, R.id.leftGraphFragment),
    Center(R.id.center_nav_graph, R.id.centerGraphFragment),
    Right(R.id.right_nav_graph, R.id.rightGraphFragment);
...
}
```

3. Insert that piece of code inside LifecycleOwner(Activity or fragment) class and provide all required data.
```
    private val multipleBackStack by lazy {
        MultipleBackstack(
            // If you inside Fragment which host of Bottom Navigation you can do this:
            // val navController by lazy { childFragmentManager.fragments.first().findNavController() }
            navController = lazy { navController },
            // Activity or Fragment 
            lifecycleOwner = this,
            // `this` if you are using Activity
            context = requireContext(),
            // Create, store and handle items inside ViewModel
            items = viewModel.getNavigationItems(),
            // Obtain events when user interacts with UI
            flowOnSelect = viewModel.currentTabFlow,
            // When you need special behaviour when user changes desctination
            onMenuChange = { ... }
        )
    }
```

4. Handle observing inside LifecycleOwner class.
```
    override fun onDestroy() {
        lifecycle.removeObserver(multipleBackStack)
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(multipleBackStack)
    }
```



