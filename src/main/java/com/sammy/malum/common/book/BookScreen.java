package com.sammy.malum.common.book;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.sammy.malum.ClientHelper;
import com.sammy.malum.MalumHelper;
import com.sammy.malum.common.book.categories.BookCategory;
import com.sammy.malum.common.book.objects.*;
import com.sammy.malum.common.book.entries.BookEntry;
import com.sammy.malum.common.book.entries.BookEntryGrouping;
import com.sammy.malum.common.book.pages.BookPage;
import com.sammy.malum.core.init.MalumSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;

public class BookScreen extends Screen
{
    public static final ResourceLocation BOOK_LECTERN_TEXTURE = MalumHelper.prefix("textures/gui/book_lectern.png");
    public static final ResourceLocation BOOK_ITEM_TEXTURE = MalumHelper.prefix("textures/gui/book_item.png");
    
    public ResourceLocation texture()
    {
        if (screen.isLectern)
        {
            return BOOK_LECTERN_TEXTURE;
        }
        else
        {
            return BOOK_ITEM_TEXTURE;
        }
    }
    
    public void setupObjects(Minecraft mc)
    {
        objects = new ArrayList<>();
        this.width = mc.getMainWindow().getScaledWidth();
        this.height = mc.getMainWindow().getScaledHeight();
        int guiLeft = (width - bookWidth) / 2;
        int guiTop = (height - bookHeight) / 2;
        
        int posX = guiLeft + 35;
        int posY = guiTop + 132;
        BackArrowObject backArrowObject = new BackArrowObject(posX, posY, 32, 18);
        objects.add(backArrowObject);
        
        posX = guiLeft + 226;
        posY = guiTop + 132;
        NextArrowObject nextArrowObject = new NextArrowObject(posX, posY, 32, 18);
        objects.add(nextArrowObject);
        
        for (int i = 0; i < MalumBookCategories.categories.size(); i++)
        {
            BookCategory category = MalumBookCategories.categories.get(i);
            posX = guiLeft - 2;
            posY = guiTop + 12 + (i * 32);
            CategoryObject categoryObject = new CategoryObject(category, posX, posY, 26, 26);
            firstCategory = categoryObject;
            posX = guiLeft + 34;
            posY = guiTop + 15;
            NameObject nameObject = (NameObject) new NameObject(category.translationKey, posX, posY, 101, 16).addSpecialPredicate((s) -> currentObject.equals(categoryObject) && currentGrouping == 0);
            objects.add(nameObject);
            for (int j = 0; j < category.groupings.size(); j++)
            {
                BookEntryGrouping grouping = category.groupings.get(j);
                for (int k = 0; k < grouping.entries.size(); k++)
                {
                    BookEntry entry = grouping.entries.get(k);
                    posX = guiLeft + 34;
                    posY = guiTop + 15 + (k * 22);
                    int skipRequirement = 4;
                    if (grouping.isFirst)
                    {
                        posY += 22;
                        skipRequirement--;
                    }
                    if (k > skipRequirement)
                    {
                        posX += 123;
                        posY -= 110;
                    }
                    
                    int finalJ = j;
                    EntryObject entryObject = (EntryObject) new EntryObject(posX, posY, 101, 16, categoryObject, entry).addSpecialPredicate((s) -> currentObject != null && s.currentObject.equals(categoryObject) && s.currentGrouping == finalJ);
                    objects.add(entryObject);
                    for (int l = 0; l < entry.links.size(); l++)
                    {
                        posX = guiLeft + 268;
                        posY = guiTop + 12 + (l * 32);
                        BookEntry linkedEntry = entry.links.get(l);
                        LinkedEntryObject linkedPageObject = (LinkedEntryObject) new LinkedEntryObject(posX, posY, 26, 26, entryObject, entry).addSpecialPredicate((s) -> currentObject != null && s.currentObject.equals(entryObject));
                        objects.add(linkedPageObject);
                    }
                }
            }
            objects.add(categoryObject);
        }
    }
    public final int bookWidth = 292;
    public final int bookHeight = 190;
    public final static BookScreen screen = new BookScreen();
    public ArrayList<BookObject> objects;
    
    public BookObject firstCategory;
    public BookObject currentObject;
    public int currentGrouping;
    
    public boolean isLectern;
    public int currentPage;
    
    protected BookScreen()
    {
        super(ClientHelper.simpleTranslatableComponent("malum.gui.book.title"));
        setupObjects(Minecraft.getInstance());
    }
    
    @Override
    public void resize(Minecraft minecraft, int width, int height)
    {
        setupObjects(minecraft);
        super.resize(minecraft, width, height);
    }
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        renderBackground(matrixStack);
        Minecraft mc = Minecraft.getInstance();
        mc.getTextureManager().bindTexture(texture());
        int guiLeft = (width - bookWidth) / 2;
        int guiTop = (height - bookHeight) / 2;
        blit(matrixStack, guiLeft, guiTop, 1, 1, bookWidth, bookHeight, 512, 512);
        for (BookObject object : objects)
        {
            if (object.canAccess(this))
            {
                if (object.draw < 20)
                {
                    object.draw++;
                }
                if (isHovering(mouseX, mouseY, object.posX, object.posY, object.width, object.height))
                {
                    if (object.hover < 20)
                    {
                        object.hover++;
                    }
                    object.isHovering = true;
                }
                else
                {
                    if (object.hover > 0)
                    {
                        object.hover--;
                    }
                    object.isHovering = false;
                }
                object.draw(mc, matrixStack, mouseX, mouseY, partialTicks);
            }
            else if (object.draw > 0)
            {
                object.draw--;
            }
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        for (BookObject object : objects)
        {
            if (object.canAccess(this))
            {
                if (isHovering((int) mouseX, (int) mouseY, object.posX, object.posY, object.width, object.height))
                {
                    object.interact(screen);
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
    
    public static BookScreen getInstance(boolean isLectern)
    {
        screen.currentObject = screen.firstCategory;
        screen.isLectern = isLectern;
        return screen;
    }
    
    public void playSound()
    {
        PlayerEntity playerEntity = Minecraft.getInstance().player;
        playerEntity.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1.0f, 1.0f);
        playerEntity.playSound(MalumSounds.BOOK_TRAVEL, SoundCategory.PLAYERS, 1.0f, 0.75f + playerEntity.world.rand.nextFloat() * 0.5f);
        
    }
    public static void openScreen(boolean isLectern)
    {
        Minecraft.getInstance().displayGuiScreen(getInstance(isLectern));
        screen.playSound();
    }
    
    public boolean isHovering(int mouseX, int mouseY, int posX, int posY, int width, int height)
    {
        return mouseX > posX && mouseX < posX + width && mouseY > posY && mouseY < posY + height;
    }
    
    public static int packColor(int alpha, int red, int green, int blue)
    {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }
    
    public static void drawWrappingText(MatrixStack mStack, ITextComponent component, int x, int y, int w, float brightness)
    {
        drawWrappingText(mStack, component.getString(), x, y, w, brightness);
    }
    public static void drawWrappingText(MatrixStack mStack, String text, int x, int y, int w, float brightness)
    {
        FontRenderer font = Minecraft.getInstance().fontRenderer;
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        String line = "";
        for (String s : words)
        {
            if (font.getStringWidth(line) + font.getStringWidth(s) > w)
            {
                lines.add(line);
                line = s + " ";
            }
            else line += s + " ";
        }
        if (!line.isEmpty()) lines.add(line);
        for (int i = 0; i < lines.size(); i++)
        {
            screen.renderPurpleText(mStack, lines.get(i), x, y + i * (font.FONT_HEIGHT + 1), brightness);
        }
    }
    
    public void renderPurpleText(MatrixStack stack, ITextComponent component, int x, int y, float brightness)
    {
        renderPurpleText(stack, component.getString(), x, y, brightness);
    }
    
    public void renderPurpleText(MatrixStack stack, String text, int x, int y, float brightness)
    {
        int r = (int) MathHelper.lerp(brightness, 112, 203);
        int g = (int) MathHelper.lerp(brightness, 30, 81);
        int b = (int) MathHelper.lerp(brightness, 169, 204);
        font.drawString(stack, text, x, y - 1, packColor(128, 255, 183, 236));
        font.drawString(stack, text, x - 1, y, packColor(128, 255, 210, 243));
        font.drawString(stack, text, x + 1, y, packColor(128, 240, 131, 232));
        font.drawString(stack, text, x, y + 1, packColor(128, 236, 110, 226));
        font.drawString(stack, text, x, y, packColor(255, r, g, b));
    }
}