package com.wielik.tetris;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Random;

@SuppressWarnings("serial")
public class Level extends Canvas {
	
	Random random;
	
	private int width = 250;
	private int height = 625;
	
	private int spawn_x = 100;
	private int spawn_y = 25;
	
	private int tileWidth;
	private int tileHeight;
	private int tileSize = 25;
	
	private boolean[][] tiles;
	private Color[][] colors;
	
	private Piece currentPiece;
	private Piece nextPiece;
	
	private int clock = 0;
	private int difficulty_level = 1;
	
	private int anim_counter = 0;
	private int anim_length = 10;
	
	private int score = 0;
	private boolean game_over;

	public Level(Tetris game) {
		super();
	
		tileWidth = (int) Math.floor(this.width / this.tileSize);
		tileHeight = (int) Math.floor(this.height / this.tileSize);
		
		tiles = new boolean[tileWidth][tileHeight];
		colors = new Color[tileWidth][tileHeight];
		
		game_over = false;
		
		setSize(width, height);
	}
	
	@Override
	public Dimension getPreferredSize() {
		Dimension prefSize = new Dimension(width, height);
		return prefSize;
	}
	
	private void removeRow(int row) {
		for(int i = 0; i < tileWidth; i++) {
			resetTile(i, row);
		}
	}
	
	public void setTile(int tileX, int tileY) {
		tiles[tileX][tileY] = true;
	}
	
	public void resetTile(int tileX, int tileY) {
		tiles[tileX][tileY] = false;
	}
	
	public void toggleTile(int tileX, int tileY) {
		tiles[tileX][tileY] = !tiles[tileX][tileY];
	}
	
	public int getTileCoordinateFromPixel(int pixelValue) {
		int tile = (int) Math.floor((pixelValue / tileSize));
		return tile;
	}
	
	private void setColorToLine(int row, Color color) {
		for(int i = 0; i < tileWidth; i++) {
			colors[i][row] = color;
		}
	}
	
	private boolean isRowComplete(int row) {
		for(int i = 0; i < tileWidth; i++) {
			if(!tiles[i][row]) return false;
		}
		return true;
	}
	
	private void moveLevelDown(int startingRow) {
		for(int i = 0; i < tileWidth; i++) {
			for(int j = startingRow; j > 0; j--) {
				tiles[i][j] = tiles[i][j-1];
				colors[i][j] = colors[i][j-1];
			}
			resetTile(i, 0);
		}
	}
	
	private Piece RandomizePiece() {
		random = new Random();
		Piece randomPiece = null;
		int r_int = random.nextInt(7);
		if(r_int == 0) randomPiece = new Piece(spawn_x, spawn_y, 25, Piece.TYPE.I);
		if(r_int == 1) randomPiece = new Piece(spawn_x, spawn_y, 25, Piece.TYPE.J);
		if(r_int == 2) randomPiece = new Piece(spawn_x, spawn_y, 25, Piece.TYPE.L);
		if(r_int == 3) randomPiece = new Piece(spawn_x, spawn_y, 25, Piece.TYPE.O);
		if(r_int == 4) randomPiece = new Piece(spawn_x, spawn_y, 25, Piece.TYPE.S);
		if(r_int == 5) randomPiece = new Piece(spawn_x, spawn_y, 25, Piece.TYPE.T);
		if(r_int == 6) randomPiece = new Piece(spawn_x, spawn_y, 25, Piece.TYPE.Z);
		return randomPiece;
	}
	
	private void readInput(Input input) {
		
		if(input.isMousePressed(1)) {
			int pixX = input.getMouseX();
			int pixY = input.getMouseY();
			if (pixX >= width || pixX < 0 || pixY < 0 || pixY >= height) return;
			setTile(getTileCoordinateFromPixel(pixX), getTileCoordinateFromPixel(pixY));
		}
		if(input.isKeyPressed(68) || input.isKeyPressed(39)) {
			//Pressed D or ->
			if(!isCollisionRight(currentPiece)) {
				currentPiece.move(1, 0);  
			}
		}
		if(input.isKeyPressed(65) || input.isKeyPressed(37)) {
			//Pressed A or <-
			if(!isCollisionLeft(currentPiece)) {
				currentPiece.move(-1, 0); 
			}	
		}
		if(input.isKeyPressed(40) || input.isKeyPressed(32) || input.isKeyPressed(83)) {
			//Pressed S or down arrow or space
			if(!isCollision(currentPiece)) {
				currentPiece.update();
			}
		}
		if(input.isKeyDown(81)) {
			//Pressed Q
			if(canRotate(currentPiece)) {
				currentPiece.rotateLeft();
			}
		}
		if(input.isKeyDown(69)) {
			//Pressed E
			if(canRotate(currentPiece)) {
				currentPiece.rotateRight();
			}
		}	
		input.update();
	}
	

	public boolean isCollisionRight(Piece piece) {
		int pieceWidth = piece.getBlocks().length; 
		int pieceHeight = piece.getBlocks()[0].length;
		
		for(int i = pieceWidth - 1; i >= 0; i--) {
			for(int j = 0; j < pieceHeight; j++) {
				if((piece.getBlocks()[i][j]) && (piece.getX() + ((i + 2) * piece.getTileSize()) > width)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isCollisionLeft(Piece piece) {
		int pieceWidth = piece.getBlocks().length; 
		int pieceHeight = piece.getBlocks()[0].length;
		
		for(int i = 0; i < pieceWidth; i++) {
			for(int j = 0; j < pieceHeight; j++) {
				if((piece.getBlocks()[i][j]) && (piece.getX() + ((i - 1) * piece.getTileSize()) < 0)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isCollision(Piece piece) {
		int pieceWidth = piece.getBlocks().length;  //szerokosc klocka w kratkach
		int pieceHeight = piece.getBlocks()[0].length; //wysokosc klocka w kratkach
		int piecePosX = getTileCoordinateFromPixel(piece.getX()); //pozycja klocka w kratkach
		int piecePosY = getTileCoordinateFromPixel(piece.getY()) + 1; //plus 1 zeby sprawdzic kolizje z elementem pod klockiem
		
		for(int i = pieceWidth - 1; i >= 0; i--) {
			for(int j = pieceHeight - 1; j >= 0 ; j--) {
				//Sprawdzenie kolizji z podloga
				if(piece.getBlocks()[i][j] && ((piece.getY() + ((j + 1) * piece.getTileSize())) >= height)) return true;
				//Sprawdzenie kolizji z polozonymi klockami
				if(piece.getBlocks()[i][j]) {
					if(tiles[piecePosX + i][piecePosY + j]) return true;
				}
			}
		}
		return false;
	}
	
	private boolean canRotate(Piece piece) {
		int pieceWidth = piece.getBlocks().length;
		
		if(piece.getX() < 0) return false;
		if(piece.getX() + (pieceWidth * piece.getTileSize()) > width) return false;
		return true;
	}
	
	public void transform(Piece piece) {
		int pieceWidth = piece.getBlocks().length;
		int pieceHeight = piece.getBlocks()[0].length;
		int piecePosX = getTileCoordinateFromPixel(piece.getX()); //pozycja klocka w kratkach
		int piecePosY = getTileCoordinateFromPixel(piece.getY());
		
		for(int i = 0; i < pieceWidth; i++) {
			for(int j = 0; j < pieceHeight; j++) {
				if(piece.getBlocks()[i][j]) {
					tiles[piecePosX + i][piecePosY + j] = true;
					colors[piecePosX + i][piecePosY + j] = piece.getColor();
				}
			}
		}
		currentPiece = null;
	}
	
	public void update(Input input) {
		if(!game_over) {
			if(currentPiece == null) {
				currentPiece = RandomizePiece();
				nextPiece = RandomizePiece();
			}
			
			clock++;
			
			if(isCollision(currentPiece)) {
				if(currentPiece.isJustSpawned()) {
					game_over = true;
					System.out.println("GAME OVER");
				}
				transform(currentPiece);
				currentPiece = nextPiece;
				nextPiece = RandomizePiece();
				System.out.println(nextPiece.getType());
			}
			else {
				if(clock >= (60 / difficulty_level)) {
					currentPiece.update();
					clock = 0;
				}
				if(clock % 3 == 0) {
					readInput(input);
				}
			}
			
			for(int i = 0; i < tileHeight; i++) {
				if(isRowComplete(i)) {
					if(anim_counter == 0) setColorToLine(i, Color.WHITE);
					anim_counter++;
					if(anim_counter >= anim_length) {
						removeRow(i);
						moveLevelDown(i);
						anim_counter = 0;
						score += 50 * difficulty_level;
						if(score > (difficulty_level * 250)) difficulty_level++;
						System.out.println("SCORE: " + score + "  LEVEL: " + difficulty_level);
					}
				}		
			}
		}
	}
	

	public void render(Renderer r) {
		for(int i = 0; i < tileWidth; i++) {
			for(int j = 0; j < tileHeight; j++)
			if(tiles[i][j]) r.renderTile(i * tileSize, j * tileSize, tileSize, colors[i][j]);
			else r.renderTile(i * tileSize, j * tileSize, tileSize, Color.LIGHT_GRAY);
		}
		if(currentPiece != null) currentPiece.render(r);
	}
}
