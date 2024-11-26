# MotionButtonAnimator

**"A Custom Animated Progress View (Button) app that animates three statuses of the Button: Idle, Loading, and Completed, with a sleek design, also showcasing a clean architecture."**

MotionButtonAnimator is an app designed to help developers craft an animated view for a download button, featuring a custom design and animation. It allows you to control the view's three statuses, Idle, Progress, and Completed, using XML. The project also uses MotionScene to animate views after opening a fragment. This project is part of the **Udacity Android Kotlin Developer Nanodegree Program**.

## Why This Project?

This project showcases the ability to build custom views, work with **Animators** and **MotionScene**, handle fragment navigation, work with dependency injection, and implement a clean and maintainable MVVM architecture—all critical skills for a mid-senior Android developer.

## Main Features of the Project

- **MVVM Architecture**: Implements an MVVM pattern for clean, maintainable architecture.
- **Custom View**: Creates a custom animated view (button) that can change its attributes using XML.
- **MotionScene**: Implements MotionScene to animate views in the download detail fragment.
- **Orientation Support**: Adapts to portrait and landscape orientations without losing data.
- **Notification Handling**: Displays and handles notifications for all Android versions.
- **Modern UI**: Uses a single-activity architecture with multiple fragments.
- **Download Manager**: Uses a download manager to download from a URL.
- **Dependency Injection**: Koin (v4) used for better modularity and testability.

## See It in Action

Watch the custom animated progress button transition between Idle, Loading, and Completed states. This GIF demonstrates the sleek animations and the MotionScene implementation.

<div align="center">
  <img src="./images/project_showcase.gif" height="666" alt="Project Showcase"/>
  <p><strong>Project Showcase</strong><br>Transitions between Idle, Loading, and Completed states, using MotionScene for views.</p>
</div>


## Project Resources

- [Starter Project Code](https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter): Get the starter code for the project.
- [Project Rubric](https://docs.google.com/document/d/1xmW1wM-Ch1pa3Ldoz5TBd-9qXCTSv9LfX2ZOECJrJjE/edit?usp=sharing): View the project rubric.
- [Android Kotlin Developer Nanodegree Program](https://www.udacity.com/course/android-kotlin-developer-nanodegree--nd940): Learn more about the full program and its related projects.

#### **Note**: Many improvements and features in this project are not included in the Project Rubric as it was initially a project for the Udacity Nanodegree Program.

## Snapshots from the App

### Phone Screens (Portrait)  

<div align="center">
<table style="width: 100%; table-layout: fixed;">
<tr>
 <td align="center" style="width: 50%;">
   <img src="./images/select_download_screen.jpg" height="666" alt="Select Download URL Screen"/>
   <p><strong>Select Download URL Screen</strong><br>Select a URL for downloading, using MVVM to save and update the status dynamically.</p>
 </td>
 <td align="center" style="width: 50%;">
   <img src="./images/project_showcase.gif" height="666" alt="Project Showcase"/>
   <p><strong>Project Showcase</strong><br>View transitions between Idle, Loading, and Completed states, also MotionScene for Views.</p>
 </td>
</tr>
</table>
</div>

### Phone Screens (Landscape)  

<div align="center">
<img src="./images/select_download_screen_landscape.jpg" width="666" height="300" alt="Select Download URL Screen in Landscape Mode"/>
<p><strong>Select Download URL Screen in Landscape Mode</strong><br>Optimized for a seamless landscape viewing experience.</p>
</div>  

## Customization

<div align="center">

### Some Attributes

| Option Name        | Format  | Description                                          |
| ------------------ | ------- | ---------------------------------------------------- |
| buttonIdleColor    | `color` | Sets the Idle color of the button                    |
| buttonLoadingColor | `color` | Sets the loading fill color of the button            |
| circleLoadingColor | `color` | Sets the color of the loading circle                 |
| textAllCaps        | `bool`  | Controls button text capitalization (default: false) |
| textStyling        | `enum`  | Choose between normal, bold, italic, bold_italic     |
| cornerSize         | `dimen` | Sets the corner radius of the button                 |
| textSize           | `dimen` | Sets the text size of the button                     |

</div>  

**Here is an example of how to use these attributes in XML:**

```xml
<com.udacity.util.AnimatedProgressButton
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:buttonIdleColor="@color/idle_color"
    app:buttonLoadingColor="@color/loading_color"
    app:circleLoadingColor="@color/circle_color"
    app:textAllCaps="false"
    app:textStyling="bold"
    app:cornerSize="8dp"
    app:textSize="16sp" />
```

## Installation Guide

This project doesn't require any previous setup. Follow these steps to run the app:

1. **Clone the Repository**: Clone the project repository using Git.
2. **Open in Android Studio**: Open the project in **Android Studio Ladybug (2024.2.1 Patch 2)** or later.
3. **Build the Project**: Use **Gradle Plugin v8.7.2** to build the project.

**Note:** Ensure you have the latest version of Android Studio and the Gradle build tool installed. Please refer to the [official guide](https://developer.android.com/studio/install) if needed.

## Included External Libraries

- **[Koin (v4)](https://github.com/InsertKoinIO/koin)**: Simplifies the implementation of dependency injection for modularity.
- **[Timber](https://github.com/JakeWharton/timber)**: Useful for lightweight and efficient logging.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for improvements or bug fixes. Please reach out if you want to add more features, such as custom animations, or expand on the MVVM pattern.

## Contact

Feel free to reach out for any collaboration opportunities or if you have any questions. I'd love to hear your thoughts and contributions!

## License

This project is open-source and licensed under the Apache 2.0 License. The LICENSE file in this repository provides more details.

