package objects;

import javafx.scene.canvas.GraphicsContext;
import java.awt.*;
import javafx.scene.paint.Color;

public class Water extends Object {


	public Water(int x_pos, int y_pos) {
		super(x_pos, y_pos);
		// TODO Auto-generated constructor stub
	}

	@Override
	public javafx.scene.paint.Paint getColor() {
		if(iota == 0)return Color.BLUE;
		if(iota < 0 )return Color.RED;
		else return Color.YELLOW;
	}

	/**
	 * throws error is the right object is not ontop
	 */
	public void onTop (Stone object) {
		try {
			if (object != null) {
				// change to stone
			}
		} catch(IllegalArgumentException e) {
			System.out.println("Wrong object");
		}

	}





}
