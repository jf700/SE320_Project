package com.digitaltherapy.cli;

import com.digitaltherapy.cli.commands.*;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class CliCommandGroups {

    private final RegisterCommand registerCommand;
    private final LoginCommand loginCommand;
    private final LogoutCommand logoutCommand;
    private final ViewSessionLibraryCommand viewSessionLibraryCommand;
    private final StartSessionCommand startSessionCommand;
    private final ViewSessionHistoryCommand viewSessionHistoryCommand;
    private final NewDiaryEntryCommand newDiaryEntryCommand;
    private final ViewDiaryEntriesCommand viewDiaryEntriesCommand;
    private final ViewDiaryInsightsCommand viewDiaryInsightsCommand;
    private final WeeklySummaryCommand weeklySummaryCommand;
    private final MonthlyTrendsCommand monthlyTrendsCommand;
    private final AchievementsCommand achievementsCommand;
    private final CopingStrategiesCommand copingStrategiesCommand;
    private final EmergencyResourcesCommand emergencyResourcesCommand;
    private final SafetyPlanCommand safetyPlanCommand;

    public CliCommandGroups(RegisterCommand r, LoginCommand li, LogoutCommand lo,
                            ViewSessionLibraryCommand vsl, StartSessionCommand ss, ViewSessionHistoryCommand vsh,
                            NewDiaryEntryCommand nd, ViewDiaryEntriesCommand vde, ViewDiaryInsightsCommand vdi,
                            WeeklySummaryCommand ws, MonthlyTrendsCommand mt, AchievementsCommand ac,
                            CopingStrategiesCommand cs, EmergencyResourcesCommand er, SafetyPlanCommand sp) {
        this.registerCommand = r; this.loginCommand = li; this.logoutCommand = lo;
        this.viewSessionLibraryCommand = vsl; this.startSessionCommand = ss; this.viewSessionHistoryCommand = vsh;
        this.newDiaryEntryCommand = nd; this.viewDiaryEntriesCommand = vde; this.viewDiaryInsightsCommand = vdi;
        this.weeklySummaryCommand = ws; this.monthlyTrendsCommand = mt; this.achievementsCommand = ac;
        this.copingStrategiesCommand = cs; this.emergencyResourcesCommand = er; this.safetyPlanCommand = sp;
    }

    public List<Command> auth()     { return List.of(registerCommand, loginCommand, logoutCommand); }
    public List<Command> sessions() { return List.of(viewSessionLibraryCommand, startSessionCommand, viewSessionHistoryCommand); }
    public List<Command> diary()    { return List.of(newDiaryEntryCommand, viewDiaryEntriesCommand, viewDiaryInsightsCommand); }
    public List<Command> progress() { return List.of(weeklySummaryCommand, monthlyTrendsCommand, achievementsCommand); }
    public List<Command> crisis()   { return List.of(copingStrategiesCommand, emergencyResourcesCommand, safetyPlanCommand); }
}
