<p align="center">
  <img src="https://i.imgur.com/dEBlhTL.png" width=500 height=200 px />
</p>

Masters of Renaissance is the final test of the _Software Engineering_ course part of the _Computer Science Degree_ at Politecnico di Milano during the Academic Year 2020/2021. <br>
Masters of Renaissance is a turn-based board game where you play as an important Florence citizen: your goal is to collect and invest resources and improve your relations with the papacy. You can take resources from the Market, use resources to buy cards that will unlock powerful productions, advance in the faithpath and activate special power thanks to leader cards.
You can play with your friends in a max of 4 players match or, if you don't have any, play a Singleplayer game against Lorenzo Il Magnifico! <br>
Have fun with this strategic game! :octocat:

<img src="https://cdn.discordapp.com/attachments/768097148477898822/859528426556620820/ciccia.jpg" width=300px height=400px align="right" />

#               DOCUMENTATION

You can check the project's documentation [here](https://riccardo-donati.github.io/)

#               HOW TO RUN

This game requires [Java SE JRE](https://www.oracle.com/it/java/technologies/javase-downloads.html) to run. <br>
Download the project and use the following command in a terminal to run the game:
````
java -jar [project_path]/shade/AM35.jar
````
You'll be asked to choose between running the Server or the Client as either CLI or GUI.
The server port is 1337 and locally its IP address is 127.0.0.1. <br>

It is highly recommended to run the CLI version of the client through WSL (Linux Terminal) and use [this font](https://github.com/riccardo-donati/ingswAM2021-Donati-DiMarco-DellAgosto/blob/master/src/main/resources/DejaVu%20Sans%20Mono%20for%20Powerline.ttf) that supports Unicode characters.<br>
If you have troubles visualizing the game interface, try going into Properties->Layout and unckeck the _Wrap text output on resize_ option and increase the _Screen buffer size_ width to at least 237.<br>
For input troubles try unchecking _Quick edit mode_ in Properties->Options.<br>

# 						  TEST COVERAGE

The following table shows the coverage of the main packages in our project. The missing percentages are due getters, setters and other auxiliary methods. 

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
| Parameter Editor | [![RED](http://placehold.it/15/f03c15/f03c15)]() |

[![GREEN](http://placehold.it/15/44bb44/44bb44)]() Implemented	&nbsp;&nbsp;&nbsp;&nbsp;
[![YELLOW](http://placehold.it/15/ffdd00/ffdd00)]() Implementing&nbsp;&nbsp;&nbsp;&nbsp;
[![RED](http://placehold.it/15/f03c15/f03c15)]() Not Implemented 


#						  DEVELOPERS

* [Riccardo Donati](https://github.com/riccardo-donati) :man_with_turban:
* [Giacomo Dell'Agosto](https://github.com/GiacomoDA) :underage:
* [Davide Di Marco](https://github.com/Davidedm99) :ribbon:
