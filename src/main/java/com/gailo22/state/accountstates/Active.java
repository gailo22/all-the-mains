package com.gailo22.state.accountstates;

import com.gailo22.state.AccountState;
import com.gailo22.state.AccountUnfrozen;

import java.math.BigDecimal;
import java.util.function.Consumer;

public class Active implements AccountState {
    private final AccountUnfrozen onUnfrozen;

    public Active(AccountUnfrozen onUnfrozen) {
        this.onUnfrozen = onUnfrozen;
    }

    @Override
    public AccountState deposit(BigDecimal amount, Consumer<BigDecimal> addToBalance) {
        addToBalance.accept(amount);
        return this;
    }

    @Override
    public AccountState withdraw(BigDecimal balance, BigDecimal amount, Consumer<BigDecimal> subtractFromBalance) {
        if (balance.compareTo(amount) >= 0) {
            subtractFromBalance.accept(amount);
        }
        return this;
    }

    @Override
    public AccountState freezeAccount() {
        return new Frozen(this.onUnfrozen);
    }

    @Override
    public AccountState holderVerified() {
        return this;
    }

    @Override
    public AccountState closeAccount() {
        return new Closed();
    }
}
