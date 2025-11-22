package com.example.droidgangwar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.droidgangwar.R
import com.example.droidgangwar.databinding.FragmentCrackhouseBinding

class CrackhouseFragment : Fragment() {

    private var _binding: FragmentCrackhouseBinding? = null
    private val binding get() = _binding!!
    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrackhouseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe game state
        gameViewModel.gameState.observe(viewLifecycleOwner) { gameState ->
            updateUI(gameState)
        }

        setupDrugTrading()
    }

    private fun updateUI(gameState: com.example.droidgangwar.model.GameState) {
        binding.apply {
            // Update status display
            moneyText.text = "$${gameState.money}"

            // Update drug prices
            weedPriceText.text = "$${gameState.drugPrices["weed"] ?: 500}"
            crackPriceText.text = "$${gameState.drugPrices["crack"] ?: 1000}"
            cokePriceText.text = "$${gameState.drugPrices["coke"] ?: 2000}"
            icePriceText.text = "$${gameState.drugPrices["ice"] ?: 1500}"
            percsPriceText.text = "$${gameState.drugPrices["percs"] ?: 800}"
            pixieDustPriceText.text = "$${gameState.drugPrices["pixie_dust"] ?: 3000}"

            // Update inventory
            weedInventoryText.text = "${gameState.drugs.weed}"
            crackInventoryText.text = "${gameState.drugs.crack}"
            cokeInventoryText.text = "${gameState.drugs.coke}"
            iceInventoryText.text = "${gameState.drugs.ice}"
            percsInventoryText.text = "${gameState.drugs.percs}"
            pixieDustInventoryText.text = "${gameState.drugs.pixieDust}"

        }
    }

    private fun setupDrugTrading() {
        binding.apply {
            // Buy buttons
            buyWeedButton.setOnClickListener { showDrugTradeDialog("weed", true) }
            buyCrackButton.setOnClickListener { showDrugTradeDialog("crack", true) }
            buyCokeButton.setOnClickListener { showDrugTradeDialog("coke", true) }
            buyIceButton.setOnClickListener { showDrugTradeDialog("ice", true) }
            buyPercsButton.setOnClickListener { showDrugTradeDialog("percs", true) }
            buyPixieDustButton.setOnClickListener { showDrugTradeDialog("pixie_dust", true) }

            // Sell buttons
            sellWeedButton.setOnClickListener { showDrugTradeDialog("weed", false) }
            sellCrackButton.setOnClickListener { showDrugTradeDialog("crack", false) }
            sellCokeButton.setOnClickListener { showDrugTradeDialog("coke", false) }
            sellIceButton.setOnClickListener { showDrugTradeDialog("ice", false) }
            sellPercsButton.setOnClickListener { showDrugTradeDialog("percs", false) }
            sellPixieDustButton.setOnClickListener { showDrugTradeDialog("pixie_dust", false) }

            // Other actions
            visitProstitutesButton.setOnClickListener {
                gameViewModel.navigateToScreen("prostitutes")
            }

            backButton.setOnClickListener {
                gameViewModel.navigateToScreen("city")
            }
        }
    }

    private fun showDrugTradeDialog(drugType: String, isBuying: Boolean) {
        val gameState = gameViewModel.gameState.value ?: return

        val dialogView = layoutInflater.inflate(R.layout.dialog_drug_trade, null)
        val quantityInput = dialogView.findViewById<android.widget.EditText>(R.id.quantityInput)
        val titleText = dialogView.findViewById<android.widget.TextView>(R.id.dialogTitle)

        val drugName = when(drugType) {
            "weed" -> "Weed"
            "crack" -> "Crack"
            "coke" -> "Cocaine"
            "ice" -> "Ice"
            "percs" -> "Percs"
            "pixie_dust" -> "Pixie Dust"
            else -> drugType
        }

        val price = gameState.drugPrices[drugType] ?: 500
        val currentInventory = when(drugType) {
            "weed" -> gameState.drugs.weed
            "crack" -> gameState.drugs.crack
            "coke" -> gameState.drugs.coke
            "ice" -> gameState.drugs.ice
            "percs" -> gameState.drugs.percs
            "pixie_dust" -> gameState.drugs.pixieDust
            else -> 0
        }

        if (isBuying) {
            titleText.text = "Buy $drugName"
            quantityInput.hint = "Quantity to buy ($${price} each)"
        } else {
            titleText.text = "Sell $drugName"
            quantityInput.hint = "Quantity to sell ($${price} each, ${currentInventory} available)"
        }

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton(if (isBuying) "Buy" else "Sell") { _, _ ->
                val quantity = quantityInput.text.toString().toIntOrNull() ?: 0

                if (quantity <= 0) {
                    showMessage("Invalid quantity!")
                    return@setPositiveButton
                }

                if (isBuying) {
                    val cost = price * quantity
                    if (cost > gameState.money) {
                        showMessage("You don't have enough money! Need $${cost}.")
                        return@setPositiveButton
                    }

                    // Buy drugs
                    when(drugType) {
                        "weed" -> gameState.drugs.weed += quantity
                        "crack" -> gameState.drugs.crack += quantity
                        "coke" -> gameState.drugs.coke += quantity
                        "ice" -> gameState.drugs.ice += quantity
                        "percs" -> gameState.drugs.percs += quantity
                        "pixie_dust" -> gameState.drugs.pixieDust += quantity
                    }
                    gameState.money -= cost

                    // Chance to recruit new member from big drug buys
                    if (cost >= 5000) {
                        if ((0..100).random() < 25) { // 25% chance
                            gameState.members += 1
                            showMessage("Word of your successful drug operation spread! A new recruit joined your gang!")
                        }
                    }

                    showMessage("Bought ${quantity} kilo(s) of $drugName for $${cost}!")
                } else {
                    // Sell drugs
                    if (quantity > currentInventory) {
                        showMessage("You don't have enough $drugName to sell!")
                        return@setPositiveButton
                    }

                    val revenue = price * quantity
                    when(drugType) {
                        "weed" -> gameState.drugs.weed -= quantity
                        "crack" -> gameState.drugs.crack -= quantity
                        "coke" -> gameState.drugs.coke -= quantity
                        "ice" -> gameState.drugs.ice -= quantity
                        "percs" -> gameState.drugs.percs -= quantity
                        "pixie_dust" -> gameState.drugs.pixieDust -= quantity
                    }
                    gameState.money += revenue

                    showMessage("Sold ${quantity} kilo(s) of $drugName for $${revenue}!")
                }

                gameViewModel.saveGameState()
                updateUI(gameState)
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
