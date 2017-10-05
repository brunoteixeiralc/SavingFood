package br.com.savingfood.wizard;

import android.content.Context;

import br.com.savingfood.wizard.model.AbstractWizardModel;
import br.com.savingfood.wizard.model.InstructionPage;
import br.com.savingfood.wizard.model.PageList;
import br.com.savingfood.wizard.model.SingleFixedChoicePage;

/**
 * Created by Ronan Lima on 24/02/2016.
 */
public class WizardQuestion extends AbstractWizardModel {
    public WizardQuestion(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(

                new InstructionPage(this, "Info"),

                new SingleFixedChoicePage(this, "Qual tipo de estabelecimento possui?")
                        .setChoices("Sou Pessoa Física", "Restaurante" ,"Lanchonete" ,"Pizzaria" ,"Bar", "Cantina", "Food Truck")
                        .setRequired(true),

                new SingleFixedChoicePage(this, "Quantas vezes faz compras por mês?")
                        .setChoices("1 vez", "2 vezes", "3 vezes", "4 ou + vezes")
                        .setRequired(true)

        );
    }
}
