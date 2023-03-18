package ru.imine.client.fancychat.smile;

import ru.imine.shared.fancychat.smile.Category;
import ru.imine.shared.fancychat.smile.Smile;
import ru.imine.shared.util.Rarity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SmileDictionary
{
    private static Collection<Category> categories = new ArrayList<>();

    public static Category getCategoryByName(String name)
    {
        for (Category category : categories)
            if (category.name.equalsIgnoreCase(name))
                return category;
        return null;
    }

    public static List<Category> getAllCategories()
    {
        if (categories.isEmpty())
            return new ArrayList<>();
        return new ArrayList<>(categories);
    }

    public static Smile getSmile(String categoryName, String name)
    {
        for (Category category : categories)
            if (category.name.equalsIgnoreCase(categoryName))
                return category.getSmileByName(name);
        return null;
    }

    public static List<Smile> getAllSmiles()
    {
        return null;
    }

    public static List<Smile> getAllSmilesByRatity(Rarity rarity)
    {
        return null;
    }

    public static void updateInfo(Collection<Category> newCategories)
    {
        categories=newCategories;
    }
}