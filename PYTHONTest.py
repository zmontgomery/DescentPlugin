class Ability:
    __slots__ = ["__name", "__damage"]
    def __init__(self, name = None, damage = 10):
        self.__name = name
        self.__damage = damage

    def getDamage(self):
        return self.__damage

    def getName(self):
        return self.__name

    def __repr__(self):
        return self.__name

class Stats:
    __slots__ = ["__abilities"]
    def __init__(self, abilities = Ability()):
        self.__abilities = abilities

    def getAbilities(self):
        return self.__abilities

class Character:
    __slots__ = ["__name", "__abilities"]
    def __init__(self, name = None, abilities = []):
        self.__name = name
        self.__abilities = abilities

    def getAbilities(self):
        return self.__abilities

    def getName(self):
        return self.__name

    def __repr__(self):
        return self.__name

class Player:
    __slots__ = ["__character"]
    def __init__(self, character = Character()):
        self.__character = character

    def getCharacter(self):
        return self.__character

def main():
    knife_throw = Ability("Knife Throw")
    punch = Ability("Punch")
    ability_list = []
    ability_list.append(knife_throw)
    ability_list.append(punch)
    shadow_man = Character("Shadow Man", ability_list)
    NuclearMario = Player(shadow_man)
    print(NuclearMario.getCharacter())
    print(NuclearMario.getCharacter().getName())
    for ability in NuclearMario.getCharacter().getAbilities():
        print(ability)

main()
