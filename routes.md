# TheHumanity API Routes

## Game API
This API is designed for access to current games.

### /api/games
Gets all of the channel names that games are currently being played in.
```json
[
  "#CAHdev",
  "#CardsAgainstHumanity",
  ...
]
```

### /api/game/{channel}
Gets the current game in a channel, including the current round.  
```/api/game/CAHdev``` – Retrieves the game in the #CAHdev channel.
```json
{
  "players": [
    "jkcclemens",
    "turt2live",
    "Kashike"
  ],
  "historicPlayers": [
    "jkcclemens",
    "turt2live",
    "Kashike"
  ],
  "houseRules": [
    "Rebooting the Universe"
  ],
  "cardPacks": [
    "v3",
    "x1"
  ],
  "currentRound": {
    "number": 2,
    "czar": "Kashike",
    "blackCard": "I can always masturbate to <BLANK>.",
    "stage": "WAITING_FOR_CZAR",
    "plays": [
      "Cock",
      "Bees?"
    ],
    "skippedPlayers": []
  },
  "scores": {
    "jkcclemens": 1,
    "turt2live": 0,
    "Kashike": 1
  },
  "channel": "#CAHdev",
  "host": "jkcclemens",
  "timestamps": {
    "started": 1428640294767
  }
}
```

### /api/game/{channel}/{round}
Gets a previous round by its numeric ID from the current game in the channel.  
```/api/game/CAHdev/2``` – Retrieves the second round (which must have already passed) from the current game in #CAHdev.
```json
{
  "number": 2,
  "timestamps": {
    "started": 1428640382306,
    "ended": 1428640386552
  },
  "blackCard": "I drink to forget <BLANK>.",
  "czar": "jkcclemens",
  "winner": "CahBotzie_Dev",
  "endCause": "CZAR_CHOSE_WINNER",
  "plays": {
    "ctest_a": [
      {
        "text": "Balls",
        "handIndex": 2
      }
    ],
    "CahBotzie_Dev": [
      {
        "text": "A Bop It®",
        "handIndex": 2
      }
    ]
  },
  "players": [
    "ctest_a",
    "CahBotzie_Dev",
    "jkcclemens"
  ],
  "skippedPlayers": [],
  "scoreDelta": {
    "ctest_a": 0,
    "CahBotzie_Dev": 1,
    "jkcclemens": 0
  },
  "votes": {}
}
```

## History API
This API is designed for access to old games.

### /api/history
Gets all of the channels that the API has recorded.
```json
[
  "#CAHdev",
  "#CardsAgainstHumanity",
  ...
]
```

### /api/history/{channel}
Gets all of the IDs for previous games in this channel.  
```/api/history/CAHdev``` – Retrieves all IDs of previously recorded games in the #CAHdev channel.
```json
[
  1,
  2,
  3,
  ...
]
```

### /api/history/{channel}/{game}
Gets a previous game.  
```/api/history/CAHdev/1``` – Retrieves the first recorded game in the #CAHdev channel, rounds excluded.
```json
{
  "players": [
    "jkcclemens"
  ],
  "historicPlayers": [
    "jkcclemens"
  ],
  "houseRules": [
    "Rebooting the Universe"
  ],
  "cardPacks": [
    "v3",
    "x1"
  ],
  "scores": {
    "jkcclemens": 0
  },
  "channel": "#CAHdev",
  "endCause": "NOT_ENOUGH_PLAYERS",
  "host": "jkcclemens",
  "timestamps": {
    "started": 1428640294767,
    "ended": 1428640339771
  },
  "rounds": 0
}

```
### /api/history/{channel}/{game}/{round}
Gets a round of a previous game.  
```/api/history/CAHdev/1/1``` – Retrieves the first round of the first recorded game in the #CAHdev channel.
```json
{
  "number": 1,
  "timestamps": {
    "started": 1428640382306,
    "ended": 1428640386552
  },
  "blackCard": "I drink to forget <BLANK>.",
  "czar": "jkcclemens",
  "winner": "CahBotzie_Dev",
  "endCause": "CZAR_CHOSE_WINNER",
  "plays": {
    "ctest_a": [
      {
        "text": "Balls",
        "handIndex": 2
      }
    ],
    "CahBotzie_Dev": [
      {
        "text": "A Bop It®",
        "handIndex": 2
      }
    ]
  },
  "players": [
    "ctest_a",
    "CahBotzie_Dev",
    "jkcclemens"
  ],
  "skippedPlayers": [],
  "scoreDelta": {
    "ctest_a": 0,
    "CahBotzie_Dev": 1,
    "jkcclemens": 0
  },
  "votes": {}
}
```

## Packs API
This API is designed for access to the bot's loaded card packs.

### /api/packs
Gets a list of card packs.
```json
[
  {
    "name": "v3",
    "description": "The third official version of Cards Against Humanity. Only one official version pack should be used, or else duplicates will be encountered.",
    "author": "Cards Against Humanity"
  },
  {
    "name": "x1",
    "description": "The first official expansion of Cards Against Humanity.",
    "author": "Cards Against Humanity"
  },
  ...
]
```

### /api/pack/{pack}
Gets a card pack and all of its cards.  
```/api/pack/x1``` – Retrieves the "x1" card pack and all of its cards.
```json
{
  "name": "x1",
  "blackCards": [
    {
      "blanks": 2,
      "text": "An international tribunal has found <BLANK> guilty of <BLANK>."
    },
    ...
  ],
  "whiteCards": [
    {
      "text": "A big black dick"
    },
    ...
  ],
  "description": "The first official expansion of Cards Against Humanity.",
  "author": "Cards Against Humanity"
}
```

## Channel API
This API is designed for access to the channels the bot is connected to.

### /api/channels
Gets a list of channels the bot is connected to.
```json
[
  "#CAHdev",
  "#CardsAgainstHumanity",
  ...
]
```

### /api/channel/{channel}
Gets a list of users in a channel the bot is connected to.  
```/api/channel/CAHdev``` – Retrieves the users in the #CAHdev channel, where the bot is connected.
```json
[
  {
    "nickname": "jkcclemens",
    "user": "jkcclemens",
    "hostname": "we.must.learn.kitteh.science"
  },
  ...
]
```

## Write API
This API is designed for write access to current games.
