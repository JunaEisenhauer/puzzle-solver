# 8 Puzzle Solver

An 8 puzzle solver to play around with breath search, depth search, iterative deepening and A* search. You can modify the grid size, select and algorithm, shuffle the puzzle and set the tile speed to watch the solution path.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine.

### Prerequisites

- git to clone the repository (alternatively, the project can be downloaded and unzipped)
- maven to build the project as a jar

### Installing and Running

1. Clone the git repository to your local machine.
2. Open the [pom.xml](pom.xml) as a maven project with an IDE of your choice
3. Open a terminal in the root directory of the project.
4. Build the maven project: `mvn package`
5. Execute the shaded jar file located in the target directory: `java -jar target/puzzlesolver-1.0.0-shaded.jar`

# What I have learned

- Building an application and an user interface with JavaFX, FXML and SceneBuilder
- Using layouts in JavaFX
- Styling an JavaFX application with CSS
- Implementing MVC design pattern with JavaFX
- Working with resource bundles for localization of a client application
- Understanding and implementing of uninformed search algorithms like breadth search, depth search, iterative deepening
- Understanding and implementing of heuristic search algorithms like A* search
- Implementing heuristic metrics like manhattan distance
