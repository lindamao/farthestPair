
package farthestpair;

import javax.swing.JFrame;
import java.awt.*;
import java.util.Random;
import java.util.ArrayList;

public class FarthestPair extends JFrame{

     int pointSize = 12;
     int numPoints = 50;
     int centre = pointSize/2;
     
     Point2D[] S = new Point2D[ numPoints ]; //the set S
     Point2D[] farthestPair = new Point2D[ 2 ]; //the two points of the farthest pair
     
     ArrayList<Point2D> convexHull = new ArrayList(); //the vertices of the convex hull of S
          
     Color convexHullColour = Color.white;
     Color genericColour = Color.yellow;

    
    //fills S with random points
    public void makeRandomPoints() {
        Random rand = new Random();
 
        for (int i = 0; i < numPoints; i++) {
            int x = 50 + rand.nextInt(500);
            int y = 50 + rand.nextInt(500);
            S[i] = new Point2D( x, y );            
        }        
    }
    
    public void paint(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, 800, 800);
        
        //draw the points in S
        g.setColor(genericColour);
        for (int i = 0; i < numPoints; i++) {
            g.fillOval((int)S[i].x, (int)S[i].y, pointSize, pointSize);
        }
        
        //draw convex hull points
        g.setColor(convexHullColour);
        for (int i = 0; i < convexHull.size(); i++) {
            double x1 = (convexHull.get(i)).x;
            double y1 = (convexHull.get(i)).y;
            g.fillOval((int)x1, (int)y1, pointSize, pointSize);
        }
        
        //draw the sides of the polygon containing the points in the convex hull
        g.setColor(convexHullColour);
        for (int i = 0; i < convexHull.size(); i++) {
            double x1 = (convexHull.get(i)).x;
            double y1 = (convexHull.get(i)).y;
            double x2;
            double y2;
            
            if (i < convexHull.size() - 1) {
                x2 = (convexHull.get(i + 1)).x;
                y2 = (convexHull.get(i + 1)).y;
            }
            
            else { //to connect last point back to first point
                x2 = (convexHull.get(0).x);
                y2 = (convexHull.get(0).y);               
            }
            
            g.drawLine((int)x1 + centre, (int)y1 + centre, (int)x2 + centre, (int)y2 + centre);
        }
        
        //draw a red line connecting the farthest pair
        g.setColor(Color.red);
        g.drawLine((int)farthestPair[0].x + centre, (int)farthestPair[0].y + centre, (int)farthestPair[1].x + centre, (int)farthestPair[1].y + centre);
    }
    
    
    public void findConvexHull() { 
        
        //finds lowest point
        double min = S[0].y;
        Point2D lowestY = S[0];
        
        for (int i = 0; i < numPoints; i++) {
            if (S[i].y < min) {
                min = S[i].y;
                lowestY = S[i];
            }
        }
        
        //adds lowestY to the convex hull
        convexHull.add(lowestY);
        lowestY.visited = true;
                
        //set up beginning vector <1,0>
        Vector v = new Vector (1, 0);
        
        Point2D p1 = lowestY; //sets first point        
        boolean finish = false; //set boolean for whether or not all points have been checked
        Point2D p2 = S[0]; //gives initial value for the next point (will change later)
        
        while (finish == false) {
            double minAngle = 2*Math.PI; //set initial minimum angle
            
            for (int i = 0; i < numPoints; i++) {
                Vector newVector = S[i].subtract(p1); //gets vector that connects the 2 points
                double angle = v.getAngle(newVector); //get angle between vectors
                if (angle < minAngle) { //finds smallest angle
                    minAngle = angle;
                    p2 = S[i]; //sets next point as the one which results in the smallest angle
                }
            }
            
            if (p2.visited == false) {
                convexHull.add(p2); //adds the next point to convex hull
                p2.visited = true;
                v = p2.subtract(p1); //gets new vector from new point to old point
                p1 = p2; //sets the point being compared to the new point
            }
            
            else {
                finish = true;
            }
        }
    }
    
    public Vector getNewVector (double x, double y, double a) {
        double x1 = x*Math.cos(a) - y*Math.sin(a);
        double y1 = y*Math.cos(a) + x*Math.sin(a);
        Vector newVector = new Vector (x1, y1);
        return newVector;
    }
    
    public void findFarthestPair_EfficientWay() {
        double furthestDistance = 0;
        
        //find highest & lowest point on convex hull
        double highestY = convexHull.get(0).y;
        int highIndex = 0;
        double lowestY = convexHull.get(0).y;
        int lowIndex = 0;
        
        for (int i = 0; i < convexHull.size(); i++) {
            Point2D p = convexHull.get(i);
            if (p.y > highestY) {
                highestY = p.y;
                highIndex = i;
            }
            else if (p.y < lowestY) {
                lowestY = p.y;
                lowIndex = i;
            }
        }
        
        //set up horizontal vectors <1,0> and <-1,0>
        Vector v1 = new Vector (1,0);
        Vector v2 = new Vector (-1,0);
        
        //find antipodal pairs
        for (int i = 0; i <= convexHull.size(); i++) {
            //get new vectors
            Vector v3 = (convexHull.get(highIndex)).subtract(convexHull.get((highIndex + 1) % convexHull.size() ));
            Vector v4 = (convexHull.get(lowIndex)).subtract(convexHull.get((lowIndex + 1) % convexHull.size() ));
            
            //find angle between horizontal vectors & new vectors
            double a1 = v3.getAngle(v1);
            double a2 = v4.getAngle(v2);
            
            //rotate the new vectors based on the smaller angle
            if (a1 < a2) {
                v1 = v3;
                v2 = getNewVector(v2.xComponent, v2.yComponent, a1); //rotate v2 based on a1 (smaller) angle
                highIndex = (highIndex + 1) % convexHull.size();
            }
            
            else {
                v2 = v4;
                v1 = getNewVector(v1.xComponent, v1.xComponent, a2); //rotate v1 based on a2 (smaller) angle
                lowIndex = (lowIndex + 1) % convexHull.size();
            }
            
            //get coordinate values for furthest pair
            double x1 = convexHull.get(highIndex).x;
            double y1 = convexHull.get(highIndex).y;
            double x2 = convexHull.get(lowIndex).x;
            double y2 = convexHull.get(lowIndex).y;
            
            double distance = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
            
            //find furthest distance & sets it
            if ( distance > furthestDistance ) {
                furthestDistance = distance;
                farthestPair[0] = convexHull.get(highIndex);
                farthestPair[1] = convexHull.get(lowIndex);
            }
            
        }
        
    }
    
    public void findFarthestPair_BruteForceWay() {
        double furthestDistance = 0;
        
        for (int i = 0; i < numPoints; i++) {
            for (int j = i + 1; j < numPoints; j++) {
                double distance = Math.sqrt(Math.pow((S[j].x - S[i].x), 2) + Math.pow((S[j].y - S[i].y), 2));
                if ( distance > furthestDistance ) {
                    furthestDistance = distance;
                    farthestPair[0] = S[i];
                    farthestPair[1] = S[j];
                }
            }
        }
    }
    
    public static void main(String[] args) {
        FarthestPair fpf = new FarthestPair();
        
        fpf.setSize(800, 800);
        fpf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        fpf.makeRandomPoints();
        
        fpf.findConvexHull();
        
        fpf.findFarthestPair_EfficientWay();
        //fpf.findFarthestPair_BruteForceWay();

        fpf.setVisible(true); 
    }
}
