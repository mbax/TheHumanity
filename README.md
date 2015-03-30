TheHumanity
===========

[![Build Status](https://travis-ci.org/RoyalDev/TheHumanity.svg?branch=master)](https://travis-ci.org/RoyalDev/TheHumanity)

*Oh, the humanity!*

An IRC bot for Cards Against Humanity.

## Building

To build TheHumanity, simply clone it and run ```mvn package```. The compiled JAR will be in the ```target``` directory.

## Running

TheHumanity is a command-line program. I may make a simple GUI for it at a later date, but never expect that to happen.

```java -jar TheHumanity.jar [flags]```

The flags are as follows:

| Flag | Required | Default | Description |
|------|----------|---------|-------------|
| -C | **Yes** | *N/A* | Card pack files to use. This expects to receive a list of file names in the ```cardpacks``` directory. |
| -c | **Yes** | *N/A* | A list of channels to join. The bot will join these channels by default. The bot can be invited to other channels after connecting. |
| -s | **Yes** | *N/A* | The server to connect to. |
| -N | No | *None* | NickServ password to identify with. This password will be sent to NickServ on joining to identify the bot. |
| -P | No | *None* | The server password to use when connecting. |
| -d | No | *None* | A list of card packs to be specified as default. Usually a version and expansions are default. |
| -n | No | TheHumanity | The nickname of the bot. If it is taken, numbers will be appended until it is not taken. |
| -p | No | 6667 | The port of the server to connect to. |
| -z | No | ! | The prefix to use for bot commands. |

## Playing

Once the bot is in a channel, a game can be started using the startgame command. The person starting the game will
automatically be added to the game, but that person may leave using the leave command. A countdown for 45 seconds will
start. In that time, players should be asked to join the game, using the join command. If at least three players are
joined after the countdown ends, the game will start.

The round number will be displayed, along with the card czar's nickname and the black card. Players may then use the
pick command to choose a card. Cards will be sent to each player using a notice. The pick command may be sent directly
to the bot or to the channel. Once all players have selected a card, the cards will be shuffled and displayed to the
channel.

The czar may then use the pick command to choose the winning card. The winner will be awarded a point, and a new round
will begin.

The game stops when the stopgame command is used, when there are no longer enough players to continue, or when there are
no longer enough white or black cards to sustain play.

## Command list

When accepting a command, arguments can be given. If multiple arguments may be supplied, they must be done so using only
spaces as a delimiter. For example, given the startgame command: ```!startgame pack_name other_pack```.

The bot supports the following commands:

| Command | Arguments | Aliases | Description |
|---------|-----------|---------|-------------|
| startgame | Card packs | start | Starts a game using the specified card packs. The names of the packs can be obtained from the packs command. |
| stopgame | *None* | stop | Stops the current game.
| join | *None* | joingame | Joins the current game. |
| leave | *None* | leavegame, part, partgame | Leaves the game. |
| pick | Card numbers | pickcard, p, pc, play, playcard | Plays or picks a card, depending on if you are the czar or a player. |
| kick | Nickname | k | Kicks a player out of the game. |
| who | *None* | *None* | Displays a color-coded list of players in the current round. Green players have played, red players have yet to play, and blue players do not need to play. |
| skip | Nickname | *None* | Marks a user as skipped for the current round. Skipped users do not have to play. |
| reboottheuniverse | Card numbers | reboot, rbtu | Allows use of the house rule *Rebooting the Universe*. This will sacrifice one point in order to replace cards out of your hand. |
| packs | *None* | *None* | Gets a list of the card packs the bot is using. |
| help | *None* | *None* | Sends a list of commands similar to this one to you over private message. |
| cards | *None* | *None* | Shows you your cards. |
| reboottheuniverse | Card numbers | rbtu, reboot | Sacrifices one point to replace the given card numbers. |
| cardcounts | "public" | cardcount, cc | Displays the current card count from all of the used card packs. If the word ```public``` is used as an argument, the count will be shown in the channel. |
| score | *None* | scores, point, points | Display the current score. |
