package com.sammy.malum.common.book.pages;

import com.ibm.icu.impl.Pair;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.sammy.malum.ClientHelper;
import com.sammy.malum.common.book.BookScreen;
import com.sammy.malum.common.book.objects.EntryObject;

import static com.sammy.malum.common.book.BookScreen.drawWrappingText;

public class TextPage extends BookPage
{
    public final String text;
    public int textWidth;
    public TextPage(String text)
    {
        this.text = "malum.gui.book.page." + text;
        this.textWidth = 124;
    }
    
    @Override
    public void draw(MatrixStack stack, EntryObject object, BookScreen screen, int mouseX, int mouseY, int guiLeft, int guiTop, boolean isSecondPage)
    {
        Pair<Integer, Integer> position = getPosition(guiLeft, guiTop, isSecondPage);
        
        drawWrappingText(stack, ClientHelper.simpleTranslatableComponent(text), position.first+4, position.second+7, textWidth, 0);
    }
    public void textWidth(int textWidth)
    {
        this.textWidth = textWidth;
    }
}