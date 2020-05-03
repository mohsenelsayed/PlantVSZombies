package GameObjects;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class SunFlower extends Plant
{
    private Sun star;
    private boolean canProduceSun;
    private float SunProductionTime;
    public SunFlower(float x,float y)
    {
        super(x,y);
        SunProductionTime = 0;
        canProduceSun = false;
        star = new Sun(x,y);
        star.setTexture(new Texture(Constants.SunSheetPath));
        animation = new Animations(Constants.SunFlowerSheetPath,Constants.SunFlowerRows,Constants.SunFlowerColumns,0.05f);
        setRectangle();
    }

    @Override
    public void setRectangle()
    {
        rectangle= new Rectangle(x,y,45,62);
    }

    public void UpdateTime()
    {
        SunProductionTime++;
        if(SunProductionTime > 800)
            canProduceSun = true;
    }
    public void ResetSun()
    {
        canProduceSun = false;
        SunProductionTime = 0;
    }
    public boolean CanProduceSun()
    {
        return canProduceSun;
    }
    public void update(float x,float y)
    {
        super.update(x,y);
        star.update(x+10,y+15);
    }
    public Sun GetSun()
    {
        return star;
    }
}
