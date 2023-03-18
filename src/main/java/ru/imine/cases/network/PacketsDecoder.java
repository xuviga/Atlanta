package ru.will0376.cases.network;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import ru.will0376.cases.pojo.Case;
import ru.will0376.cases.pojo.CaseItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PacketsDecoder {
   public static List getCases() {
      ArrayList cases = new ArrayList();
      String[] decode = Recieve.CASES_LIST.split(",");

      for (int i = 0; i < decode.length / 3; ++i) {
         String name = decode[i * 3];
         int price = Integer.parseInt(decode[i * 3 + 1]);
         String texture = decode[i * 3 + 2];
         cases.add(new Case(name, price, texture));
      }

      return cases;
   }

   public static List getCaseItemsList() {
      ArrayList items = new ArrayList();
      String[] decode = Recieve.CURRENT_CASE_ITEMS_LIST.split(",");

      for (int i = 0; i < decode.length / 3; ++i) {
         int id = Integer.parseInt(decode[i * 3]);
         int meta = Integer.parseInt(decode[i * 3 + 1]);
         int rarity = Integer.parseInt(decode[i * 3 + 2]);
         ItemStack is = new ItemStack(Item.getItemById(id));
         is.setItemDamage(meta);
         items.add(new CaseItem(is, rarity));
      }

      return items;
   }

   public static List getRandomItemsForRoll() {
      ArrayList itemsFin = new ArrayList();
      ArrayList items1 = new ArrayList();
      ArrayList items2 = new ArrayList();
      ArrayList items3 = new ArrayList();
      ArrayList items4 = new ArrayList();
      ArrayList items5 = new ArrayList();

      int decode;
      for (decode = 0; decode < getCaseItemsList().size(); ++decode) {
         switch (((CaseItem) getCaseItemsList().get(decode)).getRarity()) {
            case 1:
               items1.add(getCaseItemsList().get(decode));
               break;
            case 2:
               items2.add(getCaseItemsList().get(decode));
               break;
            case 3:
               items3.add(getCaseItemsList().get(decode));
               break;
            case 4:
               items4.add(getCaseItemsList().get(decode));
               break;
            case 5:
               items5.add(getCaseItemsList().get(decode));
         }
      }

      for (decode = 0; decode < 47; ++decode) {
         itemsFin.add(items1.get(randInt(0, items1.size() - 1)));
      }

      for (decode = 0; decode < 7; ++decode) {
         itemsFin.add(items2.get(randInt(0, items2.size() - 1)));
      }

      for (decode = 0; decode < 3; ++decode) {
         itemsFin.add(items3.get(randInt(0, items3.size() - 1)));
      }

      for (decode = 0; decode < 1; ++decode) {
         itemsFin.add(items4.get(randInt(0, items4.size() - 1)));
      }

      for (decode = 0; decode < 1; ++decode) {
         itemsFin.add(items5.get(randInt(0, items5.size() - 1)));
      }

      Collections.shuffle(itemsFin);
      String[] var8 = Recieve.WON_ITEM.split(",");
      ItemStack is = new ItemStack(Item.getItemById(Integer.parseInt(var8[0])), Integer.parseInt(var8[2]));
      is.setItemDamage(Integer.parseInt(var8[1]));
      itemsFin.add(57, new CaseItem(is, Integer.parseInt(var8[3])));
      return itemsFin;
   }

   public static int randInt(int min, int max) {
      int randomNum = (new Random()).nextInt(max - min + 1) + min;
      return randomNum;
   }

   public static float randFloat(float min, float max) {
      return min + (new Random()).nextFloat() * (max - min);
   }
}
