<p align="center">
  <img src="https://i.imgur.com/dEBlhTL.png" width=500 height=200 px />
</p>

Masters of Renaissance is the final test of the teaching: "Software Engineering", course of the "Computer Science Degree" held at Politecnico di Milano during the Academic Year 2020/2021. <br>
This is a turn based Board Game where you are in the shoes of a Florence citizen and your goal is to collect and invest resources. You can take resources from the Market, use resources to buy cards that will help you to get more resource through productions, advance in the faithpath and activate special power thanks to Leader Cards.
You can play with your friends in a max of 4 players match or, if you don't have any, play a Singleplayer game against Lorenzo Il Magnifico! <br>
Have fun with this Strategic Game! :octocat:

<img src="https://cdn.discordapp.com/attachments/768097148477898822/859528426556620820/ciccia.jpg" width=300px height=400px align="right" />

#               DOCUMENTATION

You can check the project's documentation [here](https://riccardo-donati.github.io/)

#               HOW TO RUN

Once you downloaded this project open _cmd.exe_ and type:<br> _java -jar path-to-the-project/shade/AM35.jar_ <br>
You'll be asket to choose between Running the server, the Client in CLI or GUI.
Locally the Ip will be 120.0.0.1 and the port 1337.<br>
If hosted type the Ip given by the host and leave 1337 as port number

To show game icons perfectly in CLI is recomended to run it using WSL (Terminal Linux) and installing a font that supports Unicode.

# 						  TEST CASES

The following table shows the coverage of the main Packages in our project. The missing percentage in the classes are due to useless methods like getters, setters or other methods that are not directly useful to the main skeleton of the program. 

| Package | Class | Methods | Lines |
 |:-----------------------|:------------------|:------------------------------------:|:-------------:|
 | Model | 95% (59/62) | 86% (341/393) | 86% (1648/1916) |
 | Controller | 100% (1/1) | 66% (64/96) | 48% (216/443) | 

# 						  IMPLEMENTED FUNCTIONALITIES
| Functionality | Status |
|:--------------|:------:|
| Basic Rules | [![GREEN](http://placehold.it/15/44bb44/44bb44)](https://github.com/riccardo-donati/ingswAM2021-Donati-DiMarco-DellAgosto/tree/master/src/main/java/it/polimi/ingsw/model) |
| Complete Rules | [![GREEN](http://placehold.it/15/44bb44/44bb44)](https://github.com/riccardo-donati/ingswAM2021-Donati-DiMarco-DellAgosto/tree/master/src/main/java/it/polimi/ingsw/model) |
| Socket | [![GREEN](http://placehold.it/15/44bb44/44bb44)](https://github.com/riccardo-donati/ingswAM2021-Donati-DiMarco-DellAgosto/tree/master/src/main/java/it/polimi/ingsw/network/server) |
| CLI | [![GREEN](http://placehold.it/15/44bb44/44bb44)](https://github.com/riccardo-donati/ingswAM2021-Donati-DiMarco-DellAgosto/tree/master/src/main/java/it/polimi/ingsw/network/client/CLI) |
| GUI | [![GREEN](http://placehold.it/15/44bb44/44bb44)](https://github.com/riccardo-donati/ingswAM2021-Donati-DiMarco-DellAgosto/tree/master/src/main/java/it/polimi/ingsw/network/client/GUI) |
| Multiple Matches | [![GREEN](http://placehold.it/15/44bb44/44bb44)](https://github.com/riccardo-donati/ingswAM2021-Donati-DiMarco-DellAgosto/blob/master/src/main/java/it/polimi/ingsw/network/server/Server.java) |
| Persistence | [![GREEN](http://placehold.it/15/44bb44/44bb44)](https://github.com/riccardo-donati/ingswAM2021-Donati-DiMarco-DellAgosto/tree/master/src/main/java/it/polimi/ingsw/network/server) |
| Resilience to disconnections | [![GREEN](http://placehold.it/15/44bb44/44bb44)](https://github.com/riccardo-donati/ingswAM2021-Donati-DiMarco-DellAgosto/tree/master/src/main/java/it/polimi/ingsw/network/server) |
| Local Match | [![RED](http://placehold.it/15/f03c15/f03c15)]() |
| Parameter Editor | [![RED](http://placehold.it/15/f03c15/f03c15)](https://media-cldnry.s-nbcnews.com/image/upload/t_focal-760x428,f_auto,q_auto:best/MSNBC/Components/Video/201609/a_ov_Pepe_160928.jpg) |

### LEGEND
[![GREEN](http://placehold.it/15/44bb44/44bb44)]() Implemented	&nbsp;&nbsp;&nbsp;&nbsp;[![YELLOW](http://placehold.it/15/ffdd00/ffdd00)]() Implementing&nbsp;&nbsp;&nbsp;&nbsp;[![RED](http://placehold.it/15/f03c15/f03c15)]() Not Implemented 



#						  DEVELOPERS
* [Riccardo Donati](https://github.com/riccardo-donati):man_with_turban:
* [Giacomo Dell'Agosto](https://github.com/GiacomoDA):underage:
* [Davide Di Marco](https://github.com/Davidedm99):ribbon:

