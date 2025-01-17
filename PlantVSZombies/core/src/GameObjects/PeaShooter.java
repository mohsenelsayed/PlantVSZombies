package GameObjects;
import Utils.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
public class PeaShooter extends Plant implements Attackable
{
    protected ArrayList<Bullet>bullets;
    protected float shootTime;
    public PeaShooter(float x,float y)
    {
        super(x,y);
        animation=new Animations(Constants.PeaShooterSheetPath,Constants.PeaShooterSheetRows,Constants.PeaShooterSheetColumns,0.1f);
        bullets = new ArrayList<Bullet>();
        HealthPoints = 6;
        shootTime=0;
        setRectangle();
    }
    public void AddBullet(float elapsed)
    {
        if (shootTime==0)
            shootTime=elapsed;
        else if (elapsed-shootTime>1.5f)
        {
            bullets.add(new Bullet(x+35,y+40,9));
            shootTime=0;
        }
    }
    public void CheckBullets()
    {
        Iterator<Bullet>bulletIterator = bullets.iterator();
        while(bulletIterator.hasNext()) 
        {
             Bullet bullet = bulletIterator.next();
             bullet.setRectangle();
             bullet.move();
             if(bullet.Getx() > Gdx.graphics.getWidth())
                bulletIterator.remove();
        }
    }
    private boolean checkZombie(Zombie zombie)
    {
        return (GetYindex()==zombie.GetYindex() && Getx()<zombie.Getx());
    }
    @Override
    public void Attack(float elapsed,Creature c)
    {   
         Zombie zombie =(Zombie)(c);
         if(checkZombie(zombie))
         {
               AddBullet(elapsed);
               Iterator<Bullet>bulletIterator = bullets.iterator();
               while(bulletIterator.hasNext())
               {
                    Bullet bullet = bulletIterator.next();
                    if(zombie.isTouched(bullet.GetRectangle()))
                    {
                         bulletIterator.remove();
                         zombie.isHit();
                    }
               }
         }
    }
    public ArrayList<Bullet>getBullets()
    {
        return bullets;
    }
    @Override
    public void setRectangle()
    {
        rectangle= new Rectangle(this.x+15,this.y,48,63);
    }
    @Override
    public void draw(SpriteBatch batch, float elapsed)
    {
        batch.draw((TextureRegion)getAnimation().getKeyFrame(elapsed,true),x,y);
        for(Bullet bullet:bullets)
            batch.draw((TextureRegion)bullet.getAnimation().getKeyFrame(elapsed,true),bullet.Getx(),bullet.Gety());
    }
}