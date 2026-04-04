package grupo10.olympo_academy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    private float saldo;

    public Wallet() {
    }
    
    public Wallet(float saldo) {
        this.saldo = saldo;
    }

    public void addSaldo(float amount) {
        this.saldo += amount;
    }

    public void setSaldo(float amount) {
        this.saldo = amount;
    }

    public float getSaldo(){
        return this.saldo;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }
}
