# OOP-Task2
This is the task 2 of the OOP course, and it is about Directed Weighted Graph
we have a simulator that we build for ourselfves

## UML
first we show you the UML of the Project
![umlDig](/Ex2/pics/UML.png)

## Simulator
Now it's the time for the simulator operating, to do so we first show you how the simulator is shown
First of all you have to run a this comment on Ex2.jar:
```
java -jar Ex2.jar <json-file>
```
then if the file exists and it is json, then the Form will appear, if not, the form will have no Graph

### Run Algorithm Window
the window has the options below:
 - Load graph from existing json file
 - Find the shortest path between 2 nodes
 - Is the graph conected
 - Presenting the center of the graph
 - Perform TSP on several nodes in graph (find the shortest path, thet contains all of the requested nodes)
 - Edit the Graph option

Exapmle of the window on G1.json:
![algoWind](/Ex2/pics/AlgoWindows.png)

#### Load graph
after pressing the button, select the json file that presents the Graph
after opening it the graph will be updated


#### Shortest path
select two nodes, after done choosing click on the *Finish Choosing* button to perform the shortest path,
please note that the order is crusial, and when you click on the same selected node it will removed from being choosen.
Then only the nodes and edges that involved will be shown, to the user.
Pressing on any button will refresh the graph to the origins

*Runtime = O(|V|+|E|)*


#### Center of graph
the center will be shown, after that another click on the same button will remove the presentation.

*Runtime = O(|V|x(|V|+|E|))*

#### TSP
similar to selecting the Shortest path, after done selecting it will show only the nodes and edges that involved.

*Runtime = O(|V| x (|V|+|E|)^2)*


Example of the buttons with finish choosing:

![algoB](/Ex2/pics/AlgoButtons.png)


### Edit Graph window
this will be shown after pressing the "Edit Graph" button
the window has the options below:
 - Move nodes
 - Add node to the graph
 - Remove node from the graph
 - Add edge to the graph
 - Remove edge from the graph
 - Save Changes

Exapmle of the window on G1.json:
![editWind](/Ex2/pics/EditWindows.png)

#### Move nodes
pressing on it allow you to move any node you want to anyware in the graph you would like.
All you need is to drag the Node to your wanted location.
To Enable this button press on it again.

#### Add nodes
After pressing on it, a new window will be poped up and will ask you to enter your new node
by pressing on add you will add it to the graph

#### Remove Node 
select your node that you wish to remove. after that, press on *Finish Choosing* to remove it

#### Add + Remove Edge
Similar to add and remove Node

#### Save Changes
you can save changes to a file you want, *without Json Ending*
If you made changes and not pressed this button, before closing the window, the program will ask you if you would like to save your changes.


Example of the buttons with finish choosing:

![editB](/Ex2/pics/EditButtons.png)



## Run Time Analyzing
Here are the run times:

![rtimes](/Ex2/pics/RunTimes.jpg)

