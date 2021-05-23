package descent.gamemodes;

public class Default implements Gamemode{
	
	private String name;

	@Override
	public void start() {
		name = "Default";
	}
	
	@Override
	public String toString() {
		return this.name;
	}

}
