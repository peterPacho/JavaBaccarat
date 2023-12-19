import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/*
	I need to keep value of the chips somehow and keeping it inside the button is the easiest way.
	This class also allows me to easily change the overall container type used by the images.

	It started with Button, that's why it's named like that, but now it is just a ImageView with mouseclick event handler and tooltip.install
 */

public class cButton extends ImageView implements Comparable<cButton>
{
	int value;
	double x, y; // to save the button's location
	private Tooltip tip;

	cButton ()
	{
		super();
		this.setStyle("-fx-border-radius: 2px; -fx-border-style: solid");
	}

	cButton (Image img)
	{
		super(img);
		this.setStyle("-fx-border-radius: 2px; -fx-border-style: solid");
	}

	String getTooltipText ()
	{
		if (this.tip != null)
			return this.tip.getText();
		return null;
	}

	Tooltip getTooltip ()
	{
		return this.tip;
	}

	void setTooltip (String tip)
	{
		if (this.tip != null)
			Tooltip.uninstall(this, this.tip);
		this.tip = new Tooltip(tip);
		Tooltip.install(this, this.tip);
	}

	void setTooltip (Tooltip tip)
	{
		if (this.tip != null)
			Tooltip.uninstall(this, this.tip);
		this.tip = tip;
		Tooltip.install(this, this.tip);
	}

	@Override
	public int compareTo (cButton o)
	{
		if (this.value == o.value)
			return 0;
		else if (this.value > o.value)
			return 1;

		return -1;
	}
}
