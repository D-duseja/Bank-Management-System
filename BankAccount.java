public class BankAccount {

        String name;
        long accountNumber;
        double balance;
        String password;


    public BankAccount (String n, double bal ,String password) {
        this.name=n;
        this.balance=bal;
        this.password=password;
    }


   public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }


    public void deposit(double amt){
        balance+=amt;
        System.out.println("Succesfully Deposited");
    }

    public boolean withdraw(double amt){
        if (balance>=amt) {
            balance-=amt;
            return true;
        }

        else
           return false;
    }

    public void checkBalance(){
        System.out.println("Balance Left "+balance);
    }

    public void details() {
        System.out.println("User Name "+name);
        System.out.println("Account Number " +accountNumber);
        System.out.println("Balance Left " +balance);
    }
    
}