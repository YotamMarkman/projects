package CustomThreadPoolExecutor;

public class PrimeTask implements Task<Long>{
    private int n;

    public PrimeTask(int n){
        this.n = n;
    }
    @Override
    public Long execute() {
        // TODO Auto-generated method stub
        if (n < 1) {
            throw new IllegalArgumentException("n must be >= 1");
        }
        int count = 0;
        long number = 1;
        while (count < n) {
            number++;
            if (isPrime(number)) {
                count++;
            }
        }
        return number;
    }

    private static boolean isPrime(long num) {
        if (num < 2) return false;
        if (num == 2) return true;
        if (num % 2 == 0) return false;

        for (int i = 3; i * i <= num; i += 2) {
            if (num % i == 0) return false;
        }
        return true;
    }
}
