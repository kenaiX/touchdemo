public class Test {

    public static void main(String[] args) {

        Point myPoint = new Point(200, 100);

        Point tpPoint = new Point(100, 200);

        double aa = Math.atan((double) myPoint.y / (double) myPoint.x);

        double ba = Math.atan((double) tpPoint.y / (double) tpPoint.x);

        double l = Math.cos(aa-ba) * Math.sqrt(tpPoint.x * tpPoint.x + tpPoint.y * tpPoint.y);



        System.out.print(aa*180/Math.PI+"l:" + l + "x:" + Math.sin(ba) * l + "y:" + Math.cos(ba) * l);
    }

    static class Point {

        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
