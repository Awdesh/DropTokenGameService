How to run the service
----------------------

Service is a Spring Boot Service with inbuilt tomcat running. As soon as we run the service
it exposes the port "8081".

Service can be run via -jar command

"java -jar <jar_name>"

or

Application class is the main class that starts-up the service
Using IntelliJ, we can run the Application class which will start the entire service at port 8081.

Database
--------

To persist game, players and their moves I am using in memory database
"h-2". H-2 is a relational database.

_Below are three tables inside the h-2._

**Game**
--------
id: Pk

gameId: String

rows: Integer

columns: Integer

state: String

**Move**
--------
id: Pk

type: String

column: Integer

game: Game(Fk)

player: Player(Fk)

**Player**
--------
id: Pk

playerId: String

winner: boolean

game: Game (Fk)


DropTokenGame
--------------
For each game we first create a board of specified rows and columns.
The created gameId goes inside the HashMap. Where we have each gameId as a
key and a board as a value.

e.g.

game-1 -> board[4][4]

game-2 -> board[4][4]

This allows us to check board for each game and takes decisions like if
board is full or a player has won etc.
