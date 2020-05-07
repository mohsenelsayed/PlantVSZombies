package Screens;

import GameObjects.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GameLevel implements Screen {
    private Texture SunScore;
    private BitmapFont SunScorefont;
    private Card sunflowerCard;
    private Card peashooterCard;
    private PlantsvsZombies game;
    private int wave;
    private float elapsed;
    private Plant PlacedPlant;
    private ArrayList<Plant> plants;
    private ArrayList<Zombie> zombies;
    private ArrayList<LawnMower> Mowers;
    private ArrayList<Sun> stars;
    private int score;
    private boolean visited[][];

    public GameLevel(PlantsvsZombies game) {
        this.game = game;
        SunScorefont = new BitmapFont(Gdx.files.internal("Font.fnt"));
        SunScorefont.setColor(Color.WHITE);
        SunScorefont.getData().setScale(2.6f, 3.4f);
        wave = 1;
        score = 50;
        SunScore = new Texture("star.png");
        plants = new ArrayList<Plant>();
        zombies = new ArrayList<Zombie>();
        Mowers = new ArrayList<LawnMower>();
        stars = new ArrayList<Sun>();
        visited = new boolean[10][10];
        PlacedPlant = null;
        sunflowerCard = new Card(30, 550, "sunflower.png");
        peashooterCard = new Card(30, 450, "peashooterCard.png");
        elapsed = 0;
        LoadMowers();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        elapsed += delta;
        if (elapsed >= wave * 25) {
            wave++;
            LoadZombies();
            LoadStars();
        }
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.batch.draw(game.img, 0, 0, 1254, 756);
        SunScorefont.draw(game.batch, Integer.toString(score), 100, 700);
        game.batch.draw(SunScore, 10, 640);
        game.batch.draw(sunflowerCard.getTexture(), sunflowerCard.getX(), sunflowerCard.getY(), 105, 67);
        game.batch.draw(peashooterCard.getTexture(), peashooterCard.getX(), peashooterCard.getY(), 105, 67);
        SetNewPlant();
        HandleZombies();
        HandlePlants();
        HandleStars();
        HandleMowers();
        game.batch.end();
    }
    private void HandleCollision(ArrayList object)
    {
        Iterator<Zombie> ZombieIterator = zombies.iterator();
        while(ZombieIterator.hasNext())
        {
            Zombie zombie = ZombieIterator.next(); 
            Iterator<GameObject> ObjectIterator = object.iterator();
            while(ObjectIterator.hasNext())
            {
                 GameObject gameObject = ObjectIterator.next();
                 if(gameObject instanceof PeaShooter && gameObject.GetYindex() == zombie.GetYindex())
                     ((PeaShooter)(gameObject)).Shoot(elapsed,zombie);
                 if(gameObject instanceof LawnMower && zombie.isTouched(gameObject.GetRectangle()) &&gameObject.GetYindex() == zombie.GetYindex())
                 {
                     ((LawnMower)(gameObject)).Activate();
                      zombie.Die();
                 }
                 if(gameObject instanceof Plant && zombie.isTouched(gameObject.GetRectangle()) &&gameObject.GetYindex() == zombie.GetYindex())
                 {
                     ((Plant)(gameObject)).setColliding(true);
                     ((Plant)(gameObject)).collide(elapsed);
                      zombie.setColliding(true);
                      zombie.collide(elapsed);
                      if (((Plant)(gameObject)).IsDead())
                      {
                           ObjectIterator.remove();
                           visited[gameObject.GetXindex()][gameObject.GetYindex()] = false;
                           zombie.SetCollisionTime(0);
                           zombie.setColliding(false);
                      }
                 }
                 
                 if(zombie.IsDead())
                 {
                     ZombieIterator.remove();
                     if(gameObject instanceof Plant)
                     {
                        ((Plant)(gameObject)).SetCollisionTime(0);
                        ((Plant)(gameObject)).setColliding(false);
                     }
                     break;
                 }
            }
        }
    }  
    /*private void HandleCollision(ArrayList object) 
    {
        Iterator<Zombie> ZombieIterator = zombies.iterator();
        while (ZombieIterator.hasNext()) {
            Zombie z = ZombieIterator.next();
            Iterator<GameObject> ObjectIterator = object.iterator();
            while (ObjectIterator.hasNext()) {
                GameObject gameObject = ObjectIterator.next();

                if (gameObject.GetYindex() == z.GetYindex()) {
                    if(gameObject instanceof PeaShooter)
                        PeaShooting((Zombie) z, (PeaShooter) gameObject);
                    if(z.isTouched(gameObject.get_rectangle()))
                    {
                        gameObject.setColliding(true);
                        gameObject.collide(elapsed);
                        if (gameObject instanceof LawnMower)
                            z.setHealthPoints(0);
                        else 
                        {
                            z.setColliding(true);
                            z.collide(elapsed);
                            if (gameObject.IsDead()) {
                                ObjectIterator.remove();
                                visited[gameObject.GetXindex()][gameObject.GetYindex()] = false;
                                z.SetCollisionTime(0);
                                z.setColliding(false);
                            }
                        }
                    }
                    if(z.IsDead())
                    {
                        ZombieIterator.remove();
                        gameObject.SetCollisionTime(0);
                        gameObject.setColliding(false);
                        break;
                    }
                }
            }
        }
    }
*/
    private void LoadStars() {
        Sun star = new Sun(Constants.columnPosition[new Random().nextInt(9)], 1250);
        star.setTexture(new Texture("star.png"));
        stars.add(star);
    }

    private void LoadZombies() {
        for (int i = 1; i < 4; i++) {
            NormalZombie newZombie = new NormalZombie(1170, Constants.rowPosition[i], 0.2f + (0.4f - 0.2f) * new Random().nextFloat());
            zombies.add(newZombie);
        }
    }

    private void LoadMowers() {
        for (int i = 0; i < 5; i++) {
            Mowers.add(new LawnMower(Constants.x, Constants.rowPosition[i]));
        }
    }

    private void HandleStars() {
        for (int i = 0; i < stars.size(); i++) {
            if (stars.get(i).Gety() >= 620)
                stars.get(i).update(stars.get(i).Getx(), stars.get(i).Gety() - 0.5f);
        }
        Iterator<Sun> it = stars.iterator();
        while (it.hasNext()) {
            Sun star = it.next();
            game.batch.draw(star.getTexture(), star.Getx(), star.Gety());
            if ((Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && star.IsTouched(Gdx.input.getX(), Gdx.graphics.getHeight(), Gdx.input.getY()))) {
                score += 25;
                it.remove();
            }
        }
    }

    private void HandleMowers() {
        Iterator<LawnMower> it = Mowers.iterator();
        while (it.hasNext()) {
            LawnMower mower = it.next();
            if (mower.isSetToDestroy())
                it.remove();
            mower.move();
            mower.Draw(game.batch, elapsed, mower.Getx(), mower.Gety());
            mower.setRectangle();
        }
    }

    private void HandleZombies()
    {
        for (Zombie z : zombies)
        {
            z.update(z.Getx() - z.getSpeed(), z.Gety());
            if (z.Getx() <= 265)
                game.Gameover();
            game.batch.draw((TextureRegion) z.Draw().getKeyFrame(elapsed, true), z.Getx(), z.Gety());
            z.setRectangle();
        }
        HandleCollision(plants);
        HandleCollision(Mowers);
    }

    private void HandlePlants() 
    {
        for (Plant p : plants)
        {
            game.batch.draw((TextureRegion) p.Draw().getKeyFrame(elapsed, true), p.Getx(), p.Gety());
            p.setRectangle();
            if (p instanceof PeaShooter)
            {
                Iterator<Bullet> bulletIterator = ((PeaShooter) p).getBullet().iterator();
                while (bulletIterator.hasNext()) 
                {
                    Bullet bullet = bulletIterator.next();
                    game.batch.draw((TextureRegion) bullet.Draw().getKeyFrame(elapsed, true), bullet.Getx(), bullet.Gety());
                    bullet.setRectangle();
                    bullet.move();
                    if (bullet.Getx() > Gdx.graphics.getWidth())
                        bulletIterator.remove();
                }
            }
            else if(p instanceof SunFlower)
            {
                if (!stars.contains(((SunFlower) p).GetSun()))
                    ((SunFlower) p).UpdateTime();
                if (((SunFlower) p).CanProduceSun() && !stars.contains(((SunFlower) p).GetSun())) {
                    stars.add(((SunFlower) p).GetSun());
                    ((SunFlower) p).ResetSun();
                }
            }
        }
    }

    private void SetNewPlant()
    {
        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT))
        {
            if (peashooterCard.isTouched(Gdx.input.getX(), Gdx.graphics.getHeight(), Gdx.input.getY()) && score >= 100)
                PlacedPlant = new PeaShooter(0, 0);
            if (sunflowerCard.isTouched(Gdx.input.getX(), Gdx.graphics.getHeight(), Gdx.input.getY()) && score >= 50) 
                PlacedPlant = new SunFlower(0, 0);
        }
        if(PlacedPlant != null && Gdx.input.getX() > Constants.columnPosition[0] && Gdx.input.getY() > Constants.rowPosition[0])
        {
            PlacedPlant.update(Gdx.input.getX(), 756 - Gdx.input.getY());
            PlacedPlant.update(Constants.columnPosition[PlacedPlant.GetXindex()], Constants.rowPosition[PlacedPlant.GetYindex()]);
            game.batch.setColor(Color.GRAY);
            game.batch.draw((TextureRegion) PlacedPlant.Draw().getKeyFrame(elapsed, true), PlacedPlant.Getx(), PlacedPlant.Gety());
            game.batch.setColor(Color.WHITE);
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && !visited[PlacedPlant.GetXindex()][PlacedPlant.GetYindex()]) 
            {
                score-=PlacedPlant.GetPrice();
                plants.add(PlacedPlant);
                visited[PlacedPlant.GetXindex()][PlacedPlant.GetYindex()] = true;
                PlacedPlant = null;
            }
        }
    }
    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}