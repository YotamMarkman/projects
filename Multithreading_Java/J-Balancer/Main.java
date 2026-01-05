public class Main {
    public static void main(String[] args) {
        // Entry point for the J-Balancer application
        System.out.println("J-Balancer started.");
        Mock_backend backend1 = new Mock_backend(8081, "Backend-1");
        backend1.start();   
        Mock_backend backend2 = new Mock_backend(8082, "Backend-2");
        backend2.start();
    }
}
