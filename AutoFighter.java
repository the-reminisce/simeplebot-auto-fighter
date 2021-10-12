package me.remie.xeros.combat;

import simple.api.coords.WorldArea;
import simple.api.coords.WorldPoint;
import simple.api.filters.SimpleSkills;
import simple.api.queries.SimpleEntityQuery;
import simple.api.script.Category;
import simple.api.script.Script;
import simple.api.script.ScriptManifest;
import simple.api.script.interfaces.SimplePaintable;
import simple.api.wrappers.SimpleGroundItem;
import simple.api.wrappers.SimpleItem;
import simple.api.wrappers.SimpleNpc;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

/**
 * Created by Seth on April 4/24/2021, 2021 at 4:23 PM
 *
 * @author Seth Davis <sethdavis321@gmail.com>
 * @Discord Reminisce#1707
 */
@ScriptManifest(author = "Reminisce", name = "SAIO Combat", category = Category.COMBAT, version = "0.1",
        description = "Kills monsters", discord = "Reminisce#1707", servers = { "Xeros" })
public class AutoFighter extends Script implements SimplePaintable {

    public int eatHealth;
    public int[] lootNames;
    public int[] npcIds;
    public int[] foodId;
    public boolean eatForSpace;
    public boolean quickPrayers;
    public boolean started;

    private int startExp = 0;
    public long startTime;
    public String status;
    public static final WorldArea HOME_AREA = new WorldArea(
            new WorldPoint(3072, 3521, 0), new WorldPoint(3072, 3464, 0),
            new WorldPoint(3137, 3474, 0), new WorldPoint(3137, 3521, 0));

    public AutoFighterUI gui;

    @Override
    public boolean onExecute() {
        startTime = System.currentTimeMillis();
        startExp = currentCombatXp();
        status = "Waiting to start...";
        ctx.log("Thanks for using %s!", getName());
        try {
            AutoFighter script = this;
            SwingUtilities.invokeLater(new Runnable() { @Override public void run() {
                gui = new AutoFighterUI(script);
            }});

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onProcess() {
        try {
            if (!started) { // Checks whether the script has been started or not.
                return;
            }

            if (foodId != null && (ctx.combat.health() < eatHealth)) { // If we selected food and our hp is below threshhold, let's eat
                if (eatFood()) {
                    return;
                }
            }

            if (lootNames.length > 0) {//looting has been setup

                if (eatForSpace && foodId != null && !ctx.inventory.populate().filter(foodId).isEmpty() && ctx.inventory.getFreeSlots() <= 0) {
                    eatFood();
                }

                if (ctx.inventory.getFreeSlots() > 0 && !ground().isEmpty()) {
                    SimpleGroundItem item = ground().nearest().next();
                    if (item != null) {
                        status("Looting " + item.getName());
                        item.interact();
                    }
                    return;
                }
            }
            if (foodId != null && ctx.inventory.populate().filter(foodId).isEmpty()) {
                ctx.magic.castHomeTeleport();
                ctx.onCondition(() -> HOME_AREA.containsPoint(ctx.players.getLocal().getLocation()), 150, 12);
            }

            if (!ctx.players.getLocal().inCombat()) {
                SimpleNpc fm = npcs().filter((n) -> n.getInteracting() != null && n.getInteracting().equals(ctx.players.getLocal()) && n.inCombat()).nearest().next();
                SimpleNpc npc = fm != null ? fm : npcs().nearest().next();
                if (npc == null) {
                    return;
                }
                status("Attacking " + npc.getName());
                npc.interact("attack");
                ctx.onCondition(() -> ctx.players.getLocal().inCombat(), 250, 12);
            } else {
                if (quickPrayers) {
                    handleDrinkingPrayer();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {

    }

    @Override
    public void onPaint(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(5, 2, 192, 72);
        g.setColor(Color.decode("#ea411c"));
        g.drawRect(5, 2, 192, 72);
        g.drawLine(8, 24, 194, 24);

        g.setColor(Color.decode("#e0ad01"));
        g.drawString("RAIO Fighter                              v. " + "0.1", 12, 20);
        g.drawString("Time: " + ctx.paint.formatTime(System.currentTimeMillis() - startTime), 14, 42);
        g.drawString("Status: " + status, 14, 56);
        int totalExp = currentCombatXp() - startExp;
        int expPh = (int) ((totalExp * 3600000D) / (System.currentTimeMillis() - startTime));
        g.drawString("XP: " + totalExp + " (" + expPh + ")", 14, 70);
    }

    /**
     * Drinks prayer potions when your prayer points drop below a certain amount
     */
    private void handleDrinkingPrayer() {
        if (ctx.prayers.points() > 20 && !ctx.prayers.quickPrayers()) {
            ctx.prayers.quickPrayers(true);
        }
        if (ctx.prayers.points() <= 20) {
            final SimpleItem potion = ctx.inventory.populate().filter(Pattern.compile("Prayer potion\\(\\d+\\)")).next();
            final int cached = ctx.prayers.points();
            status("Drinking prayer");
            if (potion != null && potion.interact("drink")) {
                ctx.onCondition(() -> ctx.prayers.points() > cached, 250, 12);
            }
        }
    }

    private boolean eatFood() {
        if (foodId != null) {
            final SimpleItem food = ctx.inventory.populate().filter(foodId).next();
            if (food != null) {
                final int cached = ctx.inventory.getFreeSlots();
                status("Eating " + food.getName());
                food.interact("eat");
                return ctx.onCondition(() -> ctx.inventory.getFreeSlots() > cached, 250, 9);
            }
        }
        return false;
    }

    public void setupEating(int[] foodId, int eatAt) {
        this.foodId = foodId;
        this.eatHealth = eatAt;
    }

    public void setupLooting(int[] lootNames) {
        this.lootNames = lootNames;
    }

    public void setupAttacking(int[] npcIds) {
        this.npcIds = npcIds;
    }

    public final SimpleEntityQuery<SimpleNpc> npcs() {
        return ctx.npcs.populate().filter(npcIds).filter(n -> {
            if (n == null) {
                return false;
            }
            if (n.getId() == 10) return false;
            if (n.getLocation().distanceTo(ctx.players.getLocal().getLocation()) > 15) {
                return false;
            }
            if (n.getInteracting() != null && !n.getInteracting().equals(ctx.players.getLocal())) {
                return false;
            }
            if (n.isDead()) {
                return false;
            }
            return true;
        });
    }

    public final SimpleEntityQuery<SimpleGroundItem> ground() {
        return ctx.groundItems.populate().filter(lootNames).filter(t -> {
            if (t == null) {
                return false;
            }
            if (!ctx.pathing.reachable(t.getLocation())) {
                return false;
            }
            if (t.getLocation().distanceTo(ctx.players.getLocal().getLocation()) > 15) {
                return false;
            }
            return true;
        });
    }

    public int currentCombatXp() {
        return ctx.skills.getExperience(SimpleSkills.Skill.HITPOINTS) + ctx.skills.getExperience(SimpleSkills.Skill.ATTACK)
                + ctx.skills.getExperience(SimpleSkills.Skill.STRENGTH) + ctx.skills.getExperience(SimpleSkills.Skill.DEFENCE)
                + ctx.skills.getExperience(SimpleSkills.Skill.RANGED) + ctx.skills.getExperience(SimpleSkills.Skill.MAGIC);
    }

    private void status(String status) { // Set's our script's status
        this.status = status;
    }

}
