package tetris;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.RenderingHints.Key;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Tetris1 extends JFrame implements Runnable, KeyListener{
	BufferedImage img;
	Graphics g;
	
	private Random rand = new Random();
	private int size = 30;
	private int rownum = 20;
	private int colnum = 12;
	private int offset = 50;
	private int width = colnum*size + 2*offset + 10*size;
	private int heigh = rownum*size + 2*offset;
	private int PieceColor = rand.nextInt(5);
	private Color[][] Well = new Color[rownum][colnum];
	
	private Point[][][] MyPoint = {
			{
				//I
				{new Point(0,0), new Point(1,0), new Point(2,0), new Point(3,0)},
				{new Point(0,0), new Point(0,1), new Point(0,2), new Point(0,3)},
				{new Point(0,0), new Point(1,0), new Point(2,0), new Point(3,0)},
				{new Point(0,0), new Point(0,1), new Point(0,2), new Point(0,3)}
			},
			
			{
				//J
				{new Point(0,1), new Point(1,1), new Point(2,1), new Point(2,0)},
				{new Point(1,0), new Point(1,1), new Point(1,2), new Point(2,2)},
				{new Point(0,0), new Point(1,0), new Point(2,0), new Point(0,1)},
				{new Point(1,0), new Point(1,1), new Point(1,2), new Point(0,0)}
			},
			
			{
				//L
				{new Point(0,1), new Point(1,1), new Point(2,1), new Point(1,0)},
				{new Point(0,0), new Point(0,1), new Point(0,2), new Point(1,1)},
				{new Point(0,0), new Point(1,0), new Point(2,0), new Point(1,1)},
				{new Point(1,0), new Point(1,1), new Point(1,2), new Point(0,1)}
			},
			
			{
				//O
				{new Point(0,0), new Point(0,1), new Point(1,0), new Point(1,1)},
				{new Point(0,0), new Point(0,1), new Point(1,0), new Point(1,1)},
				{new Point(0,0), new Point(0,1), new Point(1,0), new Point(1,1)},
				{new Point(0,0), new Point(0,1), new Point(1,0), new Point(1,1)}
			},
			
			{
				//Z
				{new Point(0,0), new Point(1,0), new Point(1,1), new Point(2,1)},
				{new Point(1,0), new Point(1,1), new Point(0,1), new Point(0,2)},
				{new Point(0,0), new Point(1,0), new Point(1,1), new Point(2,1)},
				{new Point(1,0), new Point(1,1), new Point(0,1), new Point(0,2)}
			},
			
		};
	
	private Color[] MyColor = {Color.ORANGE, Color.BLUE, Color.RED, Color.GREEN, Color.PINK};
	
	private Point pt = new Point(5,0);
	private int CurrentPiece = 0;
	private int Rotation = 0;
	private ArrayList<Integer> NextPiece = new ArrayList<Integer>();
	private long Score;
	
	private void SetWell()
	{
		int i,j;
		for(i=0;i<rownum;i++)
		{
			for(j=0;j<colnum;j++)
			{
				if(j==0||j==(colnum-1))
				{
					Well[i][j] = Color.YELLOW;
				}
				else
				{
					Well[i][j] = Color.BLACK;
				}		
			}
		}
		NewPiece();
	}

	private void NewPiece() 
	{
		pt.x = rand.nextInt(colnum-5)+1;
		pt.y = 0;
		CurrentPiece = rand.nextInt(5);
		Rotation = rand.nextInt(4);	
	}
	
	private boolean VerticalCollision(int x, int y, int Rotation) 
	{
		for(Point p:MyPoint[CurrentPiece][Rotation])
		{
			if((p.y+y)>=(rownum-1))
			{
				return true;
			}
			
			if(Well[pt.y+p.y+1][pt.x+p.x]!=Color.BLACK||Well[pt.y+p.y][pt.x+p.x]!=Color.BLACK)
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean HorizontalCollision(int x, int y, int Rotation) 
	{
		for(Point p:MyPoint[CurrentPiece][Rotation])
		{
			if(Well[pt.y+p.y][pt.x+p.x]!=Color.BLACK)
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean MoveHorizontalCollision(int x, int y, int Rotation, int side) 
	{
		for(Point p:MyPoint[CurrentPiece][Rotation])
		{
			if(Well[pt.y+p.y][pt.x+p.x]!=Color.BLACK||Well[pt.y+p.y][pt.x+p.x+side]!=Color.BLACK)
			{
				return true;
			}
		}
		return false;
	}
	
	private void Rotate(int i) 
	{
		int NewRotation = (Rotation+i)%4;
		if(NewRotation < 0) NewRotation += 4;
		
		if((!VerticalCollision(pt.x, pt.y, NewRotation))&&(!HorizontalCollision(pt.x, i, NewRotation)))
		{
			Rotation = NewRotation;
		}
		repaint();	
	}
	
	private void Move(int i)
	{
		if((!VerticalCollision(pt.x, pt.y, Rotation))&&(!HorizontalCollision(pt.x, pt.y, Rotation))&&(!MoveHorizontalCollision(pt.x, pt.y, Rotation, i)))
		{
			pt.x += i;
		}
		repaint();
	}
	
	private void Drop()
	{
		if(!VerticalCollision(pt.x, pt.y, Rotation))
		{
			pt.y += 1;
		}
		else
		{
			FixToWell();
		}
	}
	
	private void FixToWell()
	{
		for(Point p:MyPoint[CurrentPiece][Rotation])
		{
			Well[pt.y + p.y][pt.x+p.x] = MyColor[PieceColor];
		}		
		ClearRows();
		NewPiece();
		PieceColor = rand.nextInt(5);
	}
	
	private void DeleteRows(int row) 
	{
		System.out.println(row);
		int i,j;
		for(i=row; i>0; i--)
		{
			for(j=1; j<(colnum-1); j++)
			{
				Well[i][j] = Well[i-1][j];
			}
		}
		
		for(j=1; j<(colnum-1); j++)
		{
			Well[0][j] = Color.BLACK;
		}
		
	}
	
	private void ClearRows() 
	{
		int i,j;
		boolean gap;
		int numclear = 0;
		
		for(i=0; i<rownum; i++)
		{
			gap = false;
			for(j=1; j<(colnum-1); j++)
			{
				if(Well[i][j]==Color.BLACK)
				{
					gap = true;
					break;
				}
			}
			
			if(!gap) 
			{
				DeleteRows(i);
				numclear += 1;
			}
		}
		
		switch(numclear)
		{
		case 1:
			Score += 100;
			break;
		case 2:
			Score += 300;
			break;
		case 3:
			Score += 500;
			break;
		case 4:
			Score += 800;
			break;
			
		}	
	}

	
	private void DrawPiece(Graphics g)
	{
		g.setColor(MyColor[PieceColor]);
		for(Point p:MyPoint[CurrentPiece][Rotation])
		{
			g.fillRect((pt.x+p.x)*size + offset, (pt.y+p.y)*size + offset, size, size);
		}
	}
		
	public Tetris1() 
	{
		this.setSize(width, heigh);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SetWell();
		//System.out.println(width+" "+heigh);
		img = new BufferedImage(width+offset*2,heigh+offset*2,BufferedImage.TYPE_3BYTE_BGR);
		g = img.getGraphics();
		this.addKeyListener(this);
		this.setVisible(true);
	}
	
	public void paint(Graphics g1)
	{
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		int i,j;
		for(i=0;i<rownum;i++)
		{
			for(j=0;j<colnum;j++) 
			{
				g.setColor(Well[i][j]);
				g.fillRect(j*size + offset, i*size + offset, size, size);
				//System.out.println((j*size+offset) + " " + (i*size + offset) + " " + ((j+1)*size + offset)+" " + ((i+1)*size + offset));
			}
		}
		
		g.setColor(Color.WHITE);
		g.drawString("Score is: " + Score , 16*size + offset, offset);
		DrawPiece(g);
		
		//Duong Doc
		g.setColor(Color.WHITE);
		for(j=1;j<colnum;j++)
		{
			g.drawLine(j*size + offset, offset, j*size + offset, rownum*size+ offset);
		}
		//Duong Ngang
		g.setColor(Color.WHITE);
		for(i=1;i<rownum;i++)
		{
			g.drawLine(offset, i*size+offset, colnum*size+offset, i*size+offset);
		}
		
		g1.drawImage(img, 0, 0, null);
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true)
		{
			try
			{
				Thread.sleep(500);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
			Drop();
			this.repaint();
			if(IsGameOver())
			{
				int res = JOptionPane.showConfirmDialog(this, "Game over!!!\nRestart?", "Message", JOptionPane.YES_NO_OPTION);
				if(res == JOptionPane.YES_OPTION)
				{
					SetWell();
				}
				else
				{
					break;
				}
			}
		}
	}
	
	private boolean IsGameOver()
	{
		int j;
		for(j=1;j<(colnum-1);j++)
		{
			if(Well[0][j]!=Color.BLACK)
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) 
		{
		case KeyEvent.VK_UP:
			Rotate(1);
			break;
		case KeyEvent.VK_DOWN:
			Rotate(-1);
			break;
		case KeyEvent.VK_LEFT:
			Move(-1);
			break;
		case KeyEvent.VK_RIGHT:
			Move(1);
			break;
		case KeyEvent.VK_SPACE:
			Drop();
			repaint();          
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
