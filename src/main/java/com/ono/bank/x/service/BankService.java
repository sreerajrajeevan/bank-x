package com.ono.bank.x.service;

import com.ono.bank.x.dto.AccountDetails;
import com.ono.bank.x.dto.TransactionResponse;
import com.ono.bank.x.dto.TransferRequest;
import com.ono.bank.x.enums.AccountType;
import com.ono.bank.x.enums.TransactionType;
import com.ono.bank.x.exception.CustomerException;
import com.ono.bank.x.exception.TransactionException;
import com.ono.bank.x.model.*;
import com.ono.bank.x.dto.CustomerResponse;
import com.ono.bank.x.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BankService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    // Method to onboard a new customer with Current and Savings accounts
    public CustomerResponse onboardCustomer(Customer customer) {
        CustomerResponse customerResponse = new CustomerResponse();
        Optional<Customer> existingCustomer = customerRepository.findByEmail(customer.getEmail());
        if (existingCustomer.isPresent()) {
            throw new CustomerException("A customer with this email already exists: " + customer.getEmail());
        }

        Optional<UserRole> userRoleOptional = roleRepository.findByName("USER");
        UserRole userRole;
        if (userRoleOptional.isPresent()) {
            userRole = userRoleOptional.get();
        } else {
            userRole = new UserRole();
            userRole.setName("USER");
            userRole = roleRepository.save(userRole);
        }

        if(customer.getUser()!=null){
            customer.getUser().setUserRoles(new HashSet<>(Collections.singletonList(userRole)));
            String encodedPassword = passwordEncoder.encode(customer.getUser().getPassword());
            customer.getUser().setPassword(encodedPassword);
            userRepository.save(customer.getUser());
        }
        customer = customerRepository.save(customer);

        // curr account
        Account currentAccount = new Account();
        currentAccount.setAccountType(AccountType.CURRENT);
        currentAccount.setBalance(BigDecimal.ZERO);
        currentAccount.setCustomer(customer);
        accountRepository.save(currentAccount);
        // sav acc
        Account savingsAccount = new Account();
        savingsAccount.setAccountType(AccountType.SAVINGS);
        savingsAccount.setBalance(BigDecimal.valueOf(500));
        savingsAccount.setCustomer(customer);
        accountRepository.save(savingsAccount);

        //customer.setAccounts(List.of(currentAccount, savingsAccount));
        customer.setAccounts(new ArrayList<>(List.of(currentAccount, savingsAccount)));
        customerRepository.save(customer);
        customerResponse.setName(customer.getName());
        customerResponse.setEmail(customer.getEmail());
        customerResponse.setPhoneNumber(customer.getPhoneNumber());
        customerResponse.setAddress(customer.getAddress());
        customerResponse.setAccounts(customer.getAccounts());
        //customerResponse.setCurrentAccount(customer.getAccounts().get(0));
        //customerResponse.setSavingsAccount(customer.getAccounts().get(1));
        return customerResponse;
    }

    public TransactionResponse transferMoney(TransferRequest transferRequest) {
        TransactionResponse response = new TransactionResponse();
        Account fromAccount = accountRepository.findById(transferRequest.getFromAccountId())
                .orElseThrow(() -> new TransactionException("From account not found"));
        Account toAccount = accountRepository.findById(transferRequest.getToAccountId())
                .orElseThrow(() -> new TransactionException("To account not found"));
        BigDecimal amount = transferRequest.getAmount();

        // Check if the transfer is from a savings account
        if (AccountType.SAVINGS.equals(fromAccount.getAccountType()) && !Objects.equals(fromAccount.getCustomer().getId(), toAccount.getCustomer().getId())) {
            throw new TransactionException("You cannot transfer from a savings account to a different account.");
        }
        if(Objects.equals(fromAccount, toAccount)){
            throw new TransactionException("You cannot transfer money to same account");
        }

        // Ensure the fromAccount has sufficient balance for transfer and fee (0.05% fee)
        BigDecimal fee = amount.multiply(BigDecimal.valueOf(0.0005));  // 0.05% fee
        BigDecimal totalDeduction = amount.add(fee);  // Total deduction is amount + fee

        if (fromAccount.getBalance().compareTo(totalDeduction) < 0) {
            throw new TransactionException("Insufficient balance to complete the transfer.");
        }

        // Update the balance of the sender's account after fee deduction
        BigDecimal newFromBalance = fromAccount.getBalance().subtract(totalDeduction);
        fromAccount.setBalance(newFromBalance);

        // Handle payment into Savings Account with 0.5% interest
        BigDecimal newToBalance;
        if (AccountType.SAVINGS.equals(toAccount.getAccountType())) {
            // Apply interest on the incoming payment to a Savings Account
            BigDecimal interest = amount.multiply(BigDecimal.valueOf(0.005));  // 0.5% interest
            newToBalance = toAccount.getBalance().add(amount).add(interest);  // Credit with interest
        } else {
            newToBalance = toAccount.getBalance().add(amount);  // Standard transfer for other account types
        }

        // Update the balance of the recipient's account
        toAccount.setBalance(newToBalance);

        // Save the updated account balances
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Record the transaction with both from and to accounts
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setFee(fee);
        transaction.setAccount(fromAccount);
        transaction.setToAccountNumber(toAccount.getId());
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        //set output obj
        response.setFromAccountNumber(transaction.getAccount().getId());
        response.setToAccountNumber(transaction.getToAccountNumber());
        response.setTransactionType(transaction.getTransactionType());
        response.setAmount(transaction.getAmount());
        response.setFee(transaction.getFee());
        response.setTimestamp(transaction.getTimestamp());

        return response;
    }



    // Method to apply interest to Savings Account deposits
    public void applyInterestToSavings() {
        List<Account> savingsAccounts = accountRepository.findByAccountType(AccountType.SAVINGS);
        for (Account account : savingsAccounts) {
            BigDecimal interest = account.getBalance().multiply(BigDecimal.valueOf(0.005));  // 0.5% interest
            account.setBalance(account.getBalance().add(interest));
            accountRepository.save(account);
        }
    }

    public boolean validateCustomerCredentials(AppUser customer) {
        // Example validation (replace with your actual logic)
        Optional<AppUser> storedCustomer = userRepository.findByUsername(customer.getUsername());
        if (storedCustomer.isPresent()) {
            AppUser storedUser = storedCustomer.get();
            // Use PasswordEncoder to match hashed password
            if (passwordEncoder.matches(customer.getPassword(), storedUser.getPassword())) {
                return true;
            }
        }
        return false;
    }


    public AccountDetails getAccountById(Long accountNumber) {
        AccountDetails details= new AccountDetails();
        Optional<Account> acc = accountRepository.findById(accountNumber);
        if (acc.isPresent()){
            details.setId(acc.get().getId());
            details.setAccountType(acc.get().getAccountType());
            details.setBalance(acc.get().getBalance());
        }else{
            throw new CustomerException("There is no account present with given ID");
        }
        return details;

    }
}

