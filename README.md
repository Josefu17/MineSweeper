# Terminal Minesweeper

A text-based implementation of Minesweeper written in Java.

### Project Status

**Created:** Spring/Fall 2023 (CS Study Project).
**Status:** Currently not maintained, but I plan to revisit this project to apply modern practices and new architecture concepts.

### Future Roadmap (set on November 2025)

I intend to refactor and expand this project with the following goals:

* **Kotlin Rewrite:** Migrating the codebase from Java to Kotlin to leverage modern language features.
* **Algorithm Improvements:** Implementing "Post-Click Generation" to ensure the first click is always safe (game logic generated after user input).
* **Web Migration:** Moving the logic to a backend service (Spring Boot) and building a reactive frontend interface to replace the terminal UI.

### How to Run

1.  Compile the source:
    ```bash
    cd src
    javac minesweeper/Main.java
    ```
2.  Run the game:
    ```bash
    java minesweeper.Main
    ```