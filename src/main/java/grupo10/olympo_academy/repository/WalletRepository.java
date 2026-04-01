package grupo10.olympo_academy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import grupo10.olympo_academy.model.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long>{

    
}
