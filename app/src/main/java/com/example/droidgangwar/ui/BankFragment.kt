package com.example.droidgangwar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.droidgangwar.R
import com.example.droidgangwar.databinding.FragmentBankBinding

class BankFragment : Fragment() {

    private var _binding: FragmentBankBinding? = null
    private val binding get() = _binding!!
    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBankBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe game state
        gameViewModel.gameState.observe(viewLifecycleOwner) { gameState ->
            updateUI(gameState)
        }

        setupBankActions()
    }

    private fun updateUI(gameState: com.example.droidgangwar.model.GameState) {
        binding.apply {
            // Update status display
            moneyText.text = "$${gameState.money}"
            accountText.text = "$${gameState.account}"
            loanText.text = "$${gameState.loan}"
        }
    }

    private fun setupBankActions() {
        binding.apply {
            depositButton.setOnClickListener {
                showDepositDialog()
            }

            withdrawButton.setOnClickListener {
                showWithdrawDialog()
            }

            loanButton.setOnClickListener {
                showLoanDialog()
            }

            payLoanButton.setOnClickListener {
                showPayLoanDialog()
            }

            backButton.setOnClickListener {
                gameViewModel.navigateToScreen("city")
            }
        }
    }

    private fun showDepositDialog() {
        val gameState = gameViewModel.gameState.value ?: return

        val dialogView = layoutInflater.inflate(R.layout.dialog_bank_transaction, null)
        val amountInput = dialogView.findViewById<android.widget.EditText>(R.id.amountInput)
        val titleText = dialogView.findViewById<android.widget.TextView>(R.id.dialogTitle)

        titleText.text = "Deposit Money"
        amountInput.hint = "Amount to deposit (max: $${gameState.money})"

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Deposit") { _, _ ->
                val amount = amountInput.text.toString().toIntOrNull() ?: 0
                if (amount > 0 && amount <= gameState.money) {
                    gameState.money -= amount
                    gameState.account += amount
                    gameViewModel.saveGameState()
                    updateUI(gameState)
                    showMessage("Deposited $${amount} successfully!")
                } else {
                    showMessage("Invalid amount!")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showWithdrawDialog() {
        val gameState = gameViewModel.gameState.value ?: return

        val dialogView = layoutInflater.inflate(R.layout.dialog_bank_transaction, null)
        val amountInput = dialogView.findViewById<android.widget.EditText>(R.id.amountInput)
        val titleText = dialogView.findViewById<android.widget.TextView>(R.id.dialogTitle)

        titleText.text = "Withdraw Money"
        amountInput.hint = "Amount to withdraw (max: $${gameState.account})"

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Withdraw") { _, _ ->
                val amount = amountInput.text.toString().toIntOrNull() ?: 0
                if (amount > 0 && amount <= gameState.account) {
                    gameState.account -= amount
                    gameState.money += amount
                    gameViewModel.saveGameState()
                    updateUI(gameState)
                    showMessage("Withdrew $${amount} successfully!")
                } else {
                    showMessage("Invalid amount!")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showLoanDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_bank_loan, null)

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Get Loan") { _, _ ->
                val gameState = gameViewModel.gameState.value ?: return@setPositiveButton
                val selectedAmount = when (dialogView.findViewById<android.widget.RadioGroup>(R.id.loanOptions).checkedRadioButtonId) {
                    R.id.loan5000 -> 5000
                    R.id.loan10000 -> 10000
                    R.id.loan25000 -> 25000
                    R.id.loan50000 -> 50000
                    R.id.loan100000 -> 100000
                    else -> 0
                }

                if (selectedAmount > 0) {
                    gameState.loan += selectedAmount
                    gameState.money += selectedAmount
                    gameViewModel.saveGameState()
                    updateUI(gameState)
                    showMessage("Loan approved! You received $${selectedAmount}. Interest will be charged daily.")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showPayLoanDialog() {
        val gameState = gameViewModel.gameState.value ?: return

        if (gameState.loan <= 0) {
            showMessage("You have no outstanding loans!")
            return
        }

        val dialogView = layoutInflater.inflate(R.layout.dialog_bank_transaction, null)
        val amountInput = dialogView.findViewById<android.widget.EditText>(R.id.amountInput)
        val titleText = dialogView.findViewById<android.widget.TextView>(R.id.dialogTitle)

        titleText.text = "Pay Off Loan"
        amountInput.hint = "Amount to pay (max: $${gameState.loan})"
        amountInput.setText(gameState.loan.toString())

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Pay") { _, _ ->
                val amount = amountInput.text.toString().toIntOrNull() ?: 0
                if (amount > 0 && amount <= gameState.loan && amount <= gameState.money) {
                    gameState.loan -= amount
                    gameState.money -= amount
                    gameViewModel.saveGameState()
                    updateUI(gameState)
                    showMessage("Paid $${amount} towards your loan!")
                } else {
                    showMessage("Invalid amount!")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showMessage(message: String) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
