package com.velord.multiplebackstackapplier

/** For example:
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
xmlns:app="http://schemas.android.com/apk/res-auto"
android:id="@+id/camera_nav_graph" // this is your navigationGraphId
app:startDestination="@id/cameraGraphFragment">  // this is your startDestinationId
...
...
</navigation>
 **/
interface MultipleBackstackGraphItem {
    // Id of navigation graph for multiple backstack.
    // Graph must be included in "main|desirable|bottom" navigation graph.
    val navigationGraphId: Int
    // Use could use this id to check when you are on the start destination.
    val startDestinationId: Int
}