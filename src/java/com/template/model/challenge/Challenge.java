package com.template.model.challenge;

import java.util.List;

/**
 * Representa um desafio real de programação SQL / PostgreSQL no jogo.
 */
public class Challenge {
    private final String id;
    private final int phaseNumber;
    private final String title;
    private final String subtitle;
    private final String characterIcon;
    private final String clientNarrative;
    private final String missionObjective;
    private final String defaultSnippet;
    private final String solutionTemplate;
    private final List<String> testCriteriaDescriptions;
    private final long rewardCoins;
    private boolean completed;

    public Challenge(String id, int phaseNumber, String title, String subtitle,
                     String characterIcon, String clientNarrative, String missionObjective,
                     String defaultSnippet, String solutionTemplate,
                     List<String> testCriteriaDescriptions, long rewardCoins) {
        this.id = id;
        this.phaseNumber = phaseNumber;
        this.title = title;
        this.subtitle = subtitle;
        this.characterIcon = characterIcon;
        this.clientNarrative = clientNarrative;
        this.missionObjective = missionObjective;
        this.defaultSnippet = defaultSnippet;
        this.solutionTemplate = solutionTemplate;
        this.testCriteriaDescriptions = testCriteriaDescriptions;
        this.rewardCoins = rewardCoins;
        this.completed = false;
    }

    public String getId() {
        return id;
    }

    public int getPhaseNumber() {
        return phaseNumber;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getCharacterIcon() {
        return characterIcon;
    }

    public String getClientNarrative() {
        return clientNarrative;
    }

    public String getMissionObjective() {
        return missionObjective;
    }

    public String getDefaultSnippet() {
        return defaultSnippet;
    }

    public String getSolutionTemplate() {
        return solutionTemplate;
    }

    public List<String> getTestCriteriaDescriptions() {
        return testCriteriaDescriptions;
    }

    public long getRewardCoins() {
        return rewardCoins;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
