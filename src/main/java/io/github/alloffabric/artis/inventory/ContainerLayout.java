package io.github.alloffabric.artis.inventory;

public class ContainerLayout {

	private int craftingWidth;
	private int craftingHeight;

	private int gridWidth;
	private int gridHeight;
	private int playerWidth = 162;
	private int playerHeight = 76;

	private int gridX;
	private int gridY;
	private int catalystX;
	private int catalystY;
	private int resultX;
	private int resultY;
	private int arrowX;
	private int arrowY;
	private int playerX;
	private int playerY;

	public ContainerLayout(int gridColumns, int gridRows) {
		this.gridWidth = gridColumns * 18;
		this.catalystX = gridWidth + 10;
		this.resultX = catalystX + 18 + 14;
		this.arrowX = catalystX - 3;
		this.craftingWidth = resultX + 22;

		this.gridHeight = gridRows * 18;
		int midpoint = gridHeight / 2;
		this.arrowY = midpoint + 4;
		this.catalystY = arrowY + 21;
		this.resultY = midpoint + 4;
		this.craftingHeight = Math.max(35, gridHeight);

		this.gridY = 16;
		this.gridX = craftingWidth > playerWidth? 0 : ((playerWidth - craftingWidth) / 2) + 2;
		catalystX += gridX;
		resultX += gridX;
		arrowX += gridX;

		this.playerX = playerWidth > craftingWidth? 0 : ((craftingWidth - playerWidth) / 2) + 2;
		this.playerY = gridY + Math.max(48, gridHeight + 10);
	}

	public int getGridX() {
		return gridX;
	}

	public int getGridY() {
		return gridY;
	}

	public int getCatalystX() {
		return catalystX;
	}

	public int getCatalystY() {
		return catalystY;
	}

	public int getResultX() {
		return resultX;
	}

	public int getResultY() {
		return resultY;
	}

	public int getArrowX() {
		return arrowX;
	}

	public int getArrowY() {
		return arrowY;
	}

	public int getPlayerX() {
		return playerX;
	}

	public int getPlayerY() {
		return playerY;
	}
}
