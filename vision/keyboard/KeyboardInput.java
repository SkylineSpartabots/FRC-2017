package keyboard;


import java.awt.event.*;

public class KeyboardInput implements KeyListener
{
	private int keyCount=256;
	private boolean[] keyState;
	private KeyState[] keys;
	public KeyboardInput(int keyCount)
	{
		this.keyCount=keyCount;
		keyState=new boolean[this.keyCount];
		keys=new KeyState[keyCount];
	}
	public KeyboardInput()
	{
		keyState=new boolean[this.keyCount];
		keys=new KeyState[keyCount];
	}
	public synchronized void updateKeys()
	{
		for(int i=0;i<keyCount;i++)
		{
			if(keyState[i])
			{
				if(keys[i]==KeyState.ONCE||keys[i]==KeyState.PRESSED)
				{
					keys[i]=KeyState.PRESSED;
					//System.out.println(i+" was pressed");
				}
				else
				{
					keys[i]=KeyState.ONCE;
				}
			}
			else
			{
				keys[i]=KeyState.RELEASED;
			}
		}
	}
	public boolean keyDown(int keyCode)
	{
		return keys[keyCode]==KeyState.ONCE||keys[keyCode]==KeyState.PRESSED;
	}
	public boolean keyOnce(int keyCode)
	{
		return keys[keyCode]==KeyState.ONCE;
	}
	@Override
	public void keyPressed(KeyEvent e) 
	{
		int keyCode=e.getKeyCode();
		if(keyCode<256&&keyCode>=0)
		{
			keyState[keyCode]=true;
		}
		else
		{
			System.out.println("Keycode ("+keyCode+") is not valid");
		}
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{
		int keyCode=e.getKeyCode();
		if(keyCode<256&&keyCode>=0)
		{
			keyState[keyCode]=false;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) 
	{
		
	}

}