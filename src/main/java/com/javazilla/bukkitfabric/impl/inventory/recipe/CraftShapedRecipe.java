package com.javazilla.bukkitfabric.impl.inventory.recipe;

import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import com.javazilla.bukkitfabric.interfaces.IMixinMinecraftServer;
import com.javazilla.bukkitfabric.interfaces.IMixinRecipeManager;

import net.minecraft.recipe.Ingredient;
import net.minecraft.util.collection.DefaultedList;

public class CraftShapedRecipe extends ShapedRecipe implements RecipeInterface {

    // TODO: Could eventually use this to add a matches() method or some such
    private net.minecraft.recipe.ShapedRecipe recipe;

    public CraftShapedRecipe(NamespacedKey key, ItemStack result) {
        super(key, result);
    }

    public CraftShapedRecipe(ItemStack result, net.minecraft.recipe.ShapedRecipe recipe) {
        this(CraftNamespacedKey.fromMinecraft(recipe.getId()), result);
        this.recipe = recipe;
    }

    public static CraftShapedRecipe fromBukkitRecipe(ShapedRecipe recipe) {
        if (recipe instanceof CraftShapedRecipe) {
            return (CraftShapedRecipe) recipe;
        }
        CraftShapedRecipe ret = new CraftShapedRecipe(recipe.getKey(), recipe.getResult());
        ret.setGroup(recipe.getGroup());
        String[] shape = recipe.getShape();
        ret.shape(shape);
        Map<Character, RecipeChoice> ingredientMap = recipe.getChoiceMap();
        for (char c : ingredientMap.keySet()) {
            RecipeChoice stack = ingredientMap.get(c);
            if (stack != null) {
                ret.setIngredient(c, stack);
            }
        }
        return ret;
    }

    @Override
    public void addToCraftingManager() {
        String[] shape = this.getShape();
        Map<Character, org.bukkit.inventory.RecipeChoice> ingred = this.getChoiceMap();
        int width = shape[0].length();
        DefaultedList<Ingredient> data = DefaultedList.ofSize(shape.length * width, Ingredient.EMPTY);

        for (int i = 0; i < shape.length; i++) {
            String row = shape[i];
            for (int j = 0; j < row.length(); j++)
                data.set(i * width + j, toNMS(ingred.get(row.charAt(j)), false));
        }

        ((IMixinRecipeManager)IMixinMinecraftServer.getServer().getRecipeManager()).addRecipe(new net.minecraft.recipe.ShapedRecipe(CraftNamespacedKey.toMinecraft(this.getKey()), this.getGroup(), width, shape.length, data, CraftItemStack.asNMSCopy(this.getResult())));
    }

}