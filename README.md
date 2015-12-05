# FightClub
FightClub is a Minecraft Bukkit plugin meant for creating arenas in which users can fist fight.

### Installation
All you have to do to install FightClub is drag and drop the jar into your plugins/ folder on your server.

### Permissions
    fightclub.fight - Gives access to all user-targeted commands. /fight, /fightaccept, /listarenas
    fightclub.admin - Gives access to all arena creation commands. /createarena, /setarenaspawn

### Commands
    /fight <player name> <arena> - Challenges another user to a fight in the specified arena
    /fightaccept - Accepts a challenge sent by another player
    /listarenas - Lists available arenas.

### Admin Commands
    /createarena <name> - Creates an arena with the specified name.
    /setarenaspawn - Sets an arena spawn. Only works after /createarena has been used.