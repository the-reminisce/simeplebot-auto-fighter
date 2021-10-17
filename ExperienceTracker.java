package me.remie.xeros.utils;

import simple.api.ClientContext;
import simple.api.filters.SimpleSkills;

import java.util.LinkedList;

/**
 * Created by Seth on October 10/17/2021, 2021 at 2:03 PM
 *
 * @author Seth Davis <sethdavis321@gmail.com>
 * @Discord Reminisce#1707
 */
public class ExperienceTracker {

    protected final ClientContext ctx;
    private final LinkedList<Tracker> trackers;

    public ExperienceTracker(ClientContext ctx) {
        this.ctx = ctx;
        this.trackers = new LinkedList<>();
    }

    /**
     * Starts tracking xp said skill
     * @param skills
     */
    public void start(SimpleSkills.Skill... skills) {
        for (SimpleSkills.Skill skill : skills) {
            trackers.add(new Tracker(skill));
        }
    }

    /**
     * Starts tracking xp for all skills & clears all previously tracked data
     */
    public void startAll() {
        startAll(true);
    }

    /**
     * Starts tracking xp for all skills
     * @param clear clears all previously tracked xp data
     */
    public void startAll(boolean clear) {
        if (clear)
            trackers.clear();

        for (SimpleSkills.Skill skill : SimpleSkills.Skill.values()) {
            start(skill);
        }
    }


    /**
     *
     * @param skill
     * @return true if you're already tracking a certain skill
     */
    public boolean tracking(SimpleSkills.Skill skill) {
        for (Tracker tracker : trackers) {
            if (skill == tracker.skill) {
                return true;
            }
        }
        return false;
    }

    /**
     * Note: will never return null
     * @param skill
     * @return Returns the tracker corresponding to this skill
     */
    private Tracker getTracker(SimpleSkills.Skill skill) {
        for (Tracker tracker : trackers) {
            if (skill == tracker.skill) {
                return tracker;
            }
        }
        return null;
    }

    /**
     *
     * @param skill
     * @return elapsed time since start of tracking said skill
     */
    public long elapsed(SimpleSkills.Skill skill) {
        return tracking(skill) ? getTracker(skill).getElapsed() : 0;
    }

    /**
     *
     * @param skill
     * @return total levels gained of said skill
     */
    public int gainedLevels(SimpleSkills.Skill skill) {
        return tracking(skill) ? getTracker(skill).getGainedLevels() : 0;
    }

    /**
     *
     * @return total levels gained of said skill
     */
    public int totalGainedLevels() {
        int totalExp = 0;
        for (Tracker tracker : trackers) {
            totalExp += tracker.getGainedLevels();
        }
        return totalExp;
    }

    /**
     *
     * @param skill
     * @return total exp gained of said skill
     */
    public int gainedXP(SimpleSkills.Skill skill) {
        return tracking(skill) ? getTracker(skill).getGainedXP() : 0;
    }

    /**
     *
     * @return total exp gained of all skills
     */
    public int totalGainedXP() {
        int totalExp = 0;
        for (Tracker tracker : trackers) {
            totalExp += tracker.getGainedXP();
        }
        return totalExp;
    }

    /**
     *
     * @param skill
     * @return xph being gained of said skill
     */
    public int gainedXPPerHour(SimpleSkills.Skill skill) {
        return tracking(skill) ? getTracker(skill).getGainedXPPerHour() : 0;
    }

    /**
     *
     * @return total xph gained of all skills
     */
    public int totalGainedXPPerHour() {
        int totalExp = 0;
        for (Tracker tracker : trackers) {
            totalExp += tracker.getGainedXPPerHour();
        }
        return totalExp;
    }

    /**
     *
     * @param skill
     * @return time left till leving said skill to skill level + 1
     */
    public long timeToLevel(SimpleSkills.Skill skill) {
        return tracking(skill) ? getTracker(skill).getTimeToLevel() : 0;
    }

    /**
     * Tracking class of a said skill
     */
    private class Tracker {

        private final SimpleSkills.Skill skill;
        private final long startTime;
        private final int level, experience;

        public Tracker(SimpleSkills.Skill skill) {
            this.skill = skill;
            this.level = ctx.skills.getRealLevel(skill.getSkillIndex());
            this.experience = ctx.skills.getExperience(skill.getSkillIndex());
            this.startTime = System.currentTimeMillis();
        }

        public int getStartXP() {
            return experience;
        }

        public int getStartLevel() {
            return level;
        }

        public long getElapsed() {
            return System.currentTimeMillis() - startTime;
        }

        public int getGainedLevels() {
            return ctx.skills.getRealLevel(skill.getSkillIndex()) - level;
        }

        public int getGainedXP() {
            return ctx.skills.getExperience(skill.getSkillIndex()) - experience;
        }

        public int getGainedXPPerHour() {
            return (int) ((getGainedXP() * 3600000D) / (System.currentTimeMillis() - startTime));
        }

        public int getCurrentXP() {
            return ctx.skills.getExperience(skill.getSkillIndex());
        }

        public int getCurrentLevel() {
            return ctx.skills.getRealLevel(skill.getSkillIndex());
        }

        private long getTimeToLevel() {
            return (long) (((ctx.skills.getExperienceAt(ctx.skills.getRealLevel(skill.getSkillIndex()) + 1) - ctx.skills.getExperience(skill.getSkillIndex())) * 3600000D) / (double) getGainedXPPerHour());
        }

        /**
         * @return checks if two trackers are the same
         */
        @Override
        public boolean equals(Object obj) {
            return obj != null && (obj instanceof Tracker && (((Tracker) obj).skill.getSkillIndex() == skill.getSkillIndex()));
        }

    }

}
