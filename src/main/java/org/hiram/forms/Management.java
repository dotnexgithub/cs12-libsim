package org.hiram.forms;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.hiram.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.SpinnerNumberModel;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Management extends JFrame {
    private JPanel managementPanel;

    // Tabs
    private JPanel simulationTabPanel;
    private JPanel booksTabPanel;
    private JPanel initializeTabPanel;

    // Panels that are actually supposed to store data
    private JPanel childCurrentActionsPanel;
    private JPanel childBooksPanel;

    // Spinners
    // SpinnerNumberModel constructor used to set values, ranges, and steps for spinners
    // (https://stackoverflow.com/a/15880988)
    private final SpinnerNumberModel durationSNM = new SpinnerNumberModel(1, 1, 1000, 1);
    private final SpinnerNumberModel actionsSNM = new SpinnerNumberModel(1, 1, 1000, 1);
    private JSpinner durationSpinner;
    private JSpinner actionsSpinner;

    private JTabbedPane tabbedPane1;
    private JPanel membersTabPanel;
    private JPanel childMembersPanel;
    private JPanel childActionsInitializePanel;
    private JPanel childResultsInitializePanel;
    private JPanel initializeControlsPanel;
    public JButton startSimulationButton;
    private JCheckBox useWashroomCheckBox;
    private JScrollPane actionsInitializeScrollPane;
    private JTextArea statisticsTextArea;
    private JTextArea lastActionStatisticsTextArea;
    private JTextArea bookReturnedStatisticsTextBox;
    private JTextArea bookBorrowedStatisticsTextBox;
    private JLabel numberBooksLabel;
    private JLabel numberMembersLabel;

    // Simulation variables
    private Library library;
    private static Timer simulationTimer;
    private static int currentDay = 1;
    private static int currentAction = 0;
    private static boolean isRunning = false;
    private static int maxDays = 10;
    private static int actionsPerDay = 4;
    private static int simulationCount = 1;
    private static boolean startingNextSimulation = false;


    public Management(Library library) {
        this.library = library;

        $$$setupUI$$$();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(managementPanel);
        pack();

        // Populate Books tab with default books
        for (Book book : library.books) {
            JPanel newRow = createBookEntry(book);
            childBooksPanel.add(newRow);
        }
        numberBooksLabel.setText(library.books.size() + " books initialized");
        for (Member member : library.members) {
            JPanel newRow = createMemberEntry(member);
            childMembersPanel.add(newRow);
        }
        numberMembersLabel.setText(library.members.size() + " members initialized");
        runSimulation();
    }

    private void runSimulation() {
        startSimulationButton.addActionListener(e -> {

            // Kill previous Timers
            if (simulationTimer != null && simulationTimer.isRunning()) {
                simulationTimer.stop();
            }

            isRunning = true;
            if (startingNextSimulation) {
                clearActionsPanel();
                // Reset currentDay and currentAction
                currentDay = 1;
                currentAction = 0;
                simulationCount += 1;
                startingNextSimulation = false;
            }

            startSimulationButton.setText("Running...");
            startSimulationButton.setEnabled(false);

            // Using JSpinner to get maxDays and actionsPerDay
            maxDays = (Integer) durationSpinner.getValue();
            actionsPerDay = (Integer) actionsSpinner.getValue();

            simulationTimer = new Timer(500, evt -> {

                if (currentDay > maxDays) {
                    simulationTimer.stop();
                    isRunning = false;
                    startSimulationButton.setEnabled(true);
                    startSimulationButton.setText("Start simulation");
                    startingNextSimulation = true;
                    return;
                } else if (!isRunning && !startingNextSimulation) {
                    startSimulationButton.setEnabled(true);
                    startSimulationButton.setText("Go Next Day");
                    return;
                }

                // New Day Header
                if (currentAction == 0) {
                    addDayLabel(currentDay);
                }

                // Random Actions
                int randomAction;
                if (useWashroomCheckBox.isSelected()) {
                    randomAction = Rand.randomInt(0, 4);
                } else {
                    randomAction = Rand.randomInt(0, 3);
                }
                switch (randomAction) {
                    case 0:
                        library.randomMemberVisited();
                        actionHandler(Actions.VISIT, library);
                        break;
                    case 1:
                        library.randomMemberLoans();
                        if (library.errorLevel != 0) {
                            library.randomBookReturn();
                            if (library.errorLevel == 0 && library.focusedLoan != null) {
                                actionHandler(Actions.RETURN, library);
                            }
                        } else {
                            actionHandler(Actions.LOAN, library);
                        }
                        break;
                    case 2:
                        library.randomBookReturn();
                        if (library.errorLevel != 0) {
                            library.randomMemberLoans();
                            actionHandler(Actions.LOAN, library);
                            break;
                        }
                        actionHandler(Actions.RETURN, library);
                        break;
                    case 3:
                        library.randomMemberWashroom();
                        actionHandler(Actions.WASHROOM, library);
                        break;
                    default:
                        System.out.println("Catastrophe");
                }

                // Increment Counters
                currentAction++;
                updateStatisticsPanels();
                if (currentAction >= actionsPerDay) {
                    currentAction = 0;
                    // Pause per day
                    isRunning = false;
                    currentDay++;
                }
            });

            simulationTimer.start();
        });
    }

    private void clearActionsPanel() {
        childActionsInitializePanel.removeAll(); // from https://stackoverflow.com/a/23647385
    }


    public void actionHandler(Actions action, Library library) {
        // Sets messages and calls entry maker depending on action
        switch (action) {
            case VISIT:
                addActionEntry("Visited", Color.decode("#D3EADA"), "Member " + library.focusedMember.name +
                        " visited!");
                break;
            case LOAN:
                addActionEntry("Loaned", Color.decode("#FFA093"), "Member " + library.focusedMember.name +
                        " loaned the book " + library.focusedLoan.book.title);
                break;
            case RETURN:
                addActionEntry("Returned", Color.decode("#A0CAEF"), "Member " + library.focusedMember.name +
                        " returned the book " + library.focusedLoan.book.title);
                break;
            case WASHROOM:
                addActionEntry("Washroom", Color.decode("#EDDDC4"), "Member " + library.focusedMember.name +
                        " used the washroom.");
                break;
        }
    }


    public void addDayLabel(int day) {
        JPanel dayHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel dayLabel = new JLabel("Day " + day);
        JLabel simulationCountLabel = new JLabel("(Simulation " + simulationCount + ")");

        // Styling
        dayLabel.setFont(new Font(dayLabel.getFont().getName(), Font.BOLD, 15));
        simulationCountLabel.setFont(new Font(simulationCountLabel.getFont().getName(), Font.ITALIC, 11));
        simulationCountLabel.setForeground(Color.GRAY);

        dayHeaderPanel.add(dayLabel);
        dayHeaderPanel.add(simulationCountLabel);

        childActionsInitializePanel.add(dayHeaderPanel);
        // Bring scroll pane to bottom of page as action is added
        SwingUtilities.invokeLater(() -> snapToBottom(actionsInitializeScrollPane));
    }

    private void addActionEntry(String heading, Color identifierColour, String body) {
        JPanel rowPanel = new JPanel();

        rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.Y_AXIS));

        JTextArea actionTextArea = new JTextArea(body, 3, 1);
        JLabel headingLabel = new JLabel(heading);
        headingLabel.setFont(new Font(headingLabel.getFont().getName(), Font.BOLD, 18));
        headingLabel.setForeground(identifierColour);

        rowPanel.add(headingLabel);
        rowPanel.add(actionTextArea);

        // Set height and margins (https://stackoverflow.com/a/5894750)
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        rowPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        childActionsInitializePanel.add(rowPanel);

        // Bring scroll pane to bottom of page as action is added
        SwingUtilities.invokeLater(() -> snapToBottom(actionsInitializeScrollPane));

    }

    private JPanel createBookEntry(Book book) {
        JPanel bookEntryPanel = new JPanel();
        bookEntryPanel.setLayout(new BoxLayout(bookEntryPanel, BoxLayout.Y_AXIS));
        bookEntryPanel.setBorder(BorderFactory.createEmptyBorder(0, 2, 15, 2));

        JLabel bookTitleLabel = new JLabel(book.title);

        JTextArea bookDetailsTextArea = new JTextArea("Author: " + book.author + "    Published on " +
                book.publicationDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " by " + book.publisher +
                "    Genre: " + book.genre + "\n" + "Language: " + book.language + "    ISBN: " + book.ISBN + "\n" +
                "Quantity: " + book.quantity, 3, 1);

        // Alignment fixes
        bookTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bookDetailsTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        bookTitleLabel.setFont(new Font(bookTitleLabel.getFont().getName(), Font.BOLD, 15));

        bookEntryPanel.add(bookTitleLabel);
        bookEntryPanel.add(bookDetailsTextArea);

        // Set height
        bookEntryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        return bookEntryPanel;
    }

    private void snapToBottom(JScrollPane pane) { // from https://stackoverflow.com/a/5150437
        JScrollBar vertical = pane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    private JPanel createMemberEntry(Member member) {
        JPanel memberEntryPanel = new JPanel();
        memberEntryPanel.setLayout(new BoxLayout(memberEntryPanel, BoxLayout.Y_AXIS));
        memberEntryPanel.setBorder(BorderFactory.createEmptyBorder(0, 2, 15, 2));

        JLabel nameLabel = new JLabel(member.name);

        JTextArea memberDetailsTextArea = new JTextArea("Age: " + member.age + "    ID: " + member.id, 1, 1);

        // Alignment fixes
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        memberDetailsTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        nameLabel.setFont(new Font(nameLabel.getFont().getName(), Font.BOLD, 15));

        memberEntryPanel.add(nameLabel);
        memberEntryPanel.add(memberDetailsTextArea);

        // Set height
        memberEntryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        return memberEntryPanel;
    }

    private void updateStatisticsPanels() {
        // Panel 1: Statistics
        statisticsTextArea.setText("Visits: " + library.numberOfVisits + "\nLoans: " + library.numberOfLoans +
                "\nReturns: " + library.numberOfReturns + "\nWashroom uses: " + library.numberOfWashroomUses);
        // Panel 2: Last action
        lastActionStatisticsTextArea.setText("By " + library.focusedMember.name + " - Action " +
                library.lastAction.toString());

        // Panel 3: Last loaned
        if (library.lastAction == Actions.LOAN) bookBorrowedStatisticsTextBox.setText("Book `" +
                library.focusedLoan.book.title + "` borrowed by " + library.focusedLoan.member.name);

        // Panel 4: Last returned
        if (library.lastAction == Actions.RETURN) bookReturnedStatisticsTextBox.setText("Book `" +
                library.focusedLoan.book.title + "` returned by " + library.focusedLoan.member.name);
    }

    private void createUIComponents() {
        // Initialize custom components
        childBooksPanel = new JPanel();
        childMembersPanel = new JPanel();
        childActionsInitializePanel = new JPanel();
        actionsInitializeScrollPane = new JScrollPane();

        // SpinnerNumberModel objects used to set ranges for spinners
        durationSpinner = new JSpinner(durationSNM);
        actionsSpinner = new JSpinner(actionsSNM);

        // Set layouts to prevent NullPointerExceptions
        childBooksPanel.setLayout(new BoxLayout(childBooksPanel, BoxLayout.Y_AXIS));
        childMembersPanel.setLayout(new BoxLayout(childMembersPanel, BoxLayout.Y_AXIS));
        childActionsInitializePanel.setLayout(new BoxLayout(childActionsInitializePanel, BoxLayout.Y_AXIS));
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        managementPanel = new JPanel();
        managementPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        managementPanel.setMinimumSize(new Dimension(10, 10));
        managementPanel.setOpaque(true);
        managementPanel.setPreferredSize(new Dimension(540, 360));
        managementPanel.setRequestFocusEnabled(true);
        final JLabel label1 = new JLabel();
        label1.setText("Management");
        managementPanel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        managementPanel.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 700), null, 0, false));
        tabbedPane1 = new JTabbedPane();
        tabbedPane1.setOpaque(false);
        tabbedPane1.setPreferredSize(new Dimension(540, 360));
        tabbedPane1.setRequestFocusEnabled(true);
        scrollPane1.setViewportView(tabbedPane1);
        simulationTabPanel = new JPanel();
        simulationTabPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(5, 6, new Insets(0, 0, 0, 0), -1, -1));
        simulationTabPanel.setPreferredSize(new Dimension(600, 400));
        tabbedPane1.addTab("Simulation", simulationTabPanel);
        simulationTabPanel.add(durationSpinner, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 6, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        simulationTabPanel.add(scrollPane2, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 6, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane2.setBorder(BorderFactory.createTitledBorder(null, "Current Actions", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        childCurrentActionsPanel = new JPanel();
        childCurrentActionsPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane2.setViewportView(childCurrentActionsPanel);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        childCurrentActionsPanel.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Visit (always enabled)");
        panel1.add(label2, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JCheckBox checkBox1 = new JCheckBox();
        checkBox1.setEnabled(false);
        checkBox1.setSelected(true);
        checkBox1.setText("");
        panel1.add(checkBox1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        childCurrentActionsPanel.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Loan Book (always enabled)");
        panel2.add(label3, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer2 = new com.intellij.uiDesigner.core.Spacer();
        panel2.add(spacer2, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JCheckBox checkBox2 = new JCheckBox();
        checkBox2.setEnabled(false);
        checkBox2.setSelected(true);
        checkBox2.setText("");
        panel2.add(checkBox2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        childCurrentActionsPanel.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Return Book (always enabled)");
        panel3.add(label4, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer3 = new com.intellij.uiDesigner.core.Spacer();
        panel3.add(spacer3, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JCheckBox checkBox3 = new JCheckBox();
        checkBox3.setEnabled(false);
        checkBox3.setSelected(true);
        checkBox3.setText("");
        panel3.add(checkBox3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        childCurrentActionsPanel.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Use Washroom");
        panel4.add(label5, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer4 = new com.intellij.uiDesigner.core.Spacer();
        panel4.add(spacer4, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        useWashroomCheckBox = new JCheckBox();
        useWashroomCheckBox.setEnabled(true);
        useWashroomCheckBox.setFocusable(true);
        useWashroomCheckBox.setSelected(true);
        useWashroomCheckBox.setText("");
        panel4.add(useWashroomCheckBox, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Duration (in days)");
        simulationTabPanel.add(label6, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Actions per Day");
        simulationTabPanel.add(label7, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 6, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        simulationTabPanel.add(actionsSpinner, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 6, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        booksTabPanel = new JPanel();
        booksTabPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Books", booksTabPanel);
        final JScrollPane scrollPane3 = new JScrollPane();
        booksTabPanel.add(scrollPane3, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane3.setBorder(BorderFactory.createTitledBorder(null, "Books", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        scrollPane3.setViewportView(childBooksPanel);
        numberBooksLabel = new JLabel();
        numberBooksLabel.setFocusTraversalPolicyProvider(false);
        Font numberBooksLabelFont = this.$$$getFont$$$(null, Font.BOLD, 20, numberBooksLabel.getFont());
        if (numberBooksLabelFont != null) numberBooksLabel.setFont(numberBooksLabelFont);
        numberBooksLabel.setOpaque(false);
        numberBooksLabel.setText("0 books initialized");
        booksTabPanel.add(numberBooksLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        membersTabPanel = new JPanel();
        membersTabPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Members", membersTabPanel);
        final JScrollPane scrollPane4 = new JScrollPane();
        membersTabPanel.add(scrollPane4, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane4.setBorder(BorderFactory.createTitledBorder(null, "Members", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        scrollPane4.setViewportView(childMembersPanel);
        numberMembersLabel = new JLabel();
        Font numberMembersLabelFont = this.$$$getFont$$$(null, Font.BOLD, 20, numberMembersLabel.getFont());
        if (numberMembersLabelFont != null) numberMembersLabel.setFont(numberMembersLabelFont);
        numberMembersLabel.setText("0 members initialized");
        membersTabPanel.add(numberMembersLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        initializeTabPanel = new JPanel();
        initializeTabPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Initialize", initializeTabPanel);
        final com.intellij.uiDesigner.core.Spacer spacer5 = new com.intellij.uiDesigner.core.Spacer();
        initializeTabPanel.add(spacer5, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer6 = new com.intellij.uiDesigner.core.Spacer();
        initializeTabPanel.add(spacer6, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        initializeTabPanel.add(actionsInitializeScrollPane, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(400, 300), null, 0, false));
        childActionsInitializePanel.setMinimumSize(new Dimension(0, 0));
        actionsInitializeScrollPane.setViewportView(childActionsInitializePanel);
        childActionsInitializePanel.setBorder(BorderFactory.createTitledBorder(null, "Action View", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JScrollPane scrollPane5 = new JScrollPane();
        initializeTabPanel.add(scrollPane5, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        childResultsInitializePanel = new JPanel();
        childResultsInitializePanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane5.setViewportView(childResultsInitializePanel);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        childResultsInitializePanel.add(panel5, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        Font label8Font = this.$$$getFont$$$(null, Font.BOLD, 16, label8.getFont());
        if (label8Font != null) label8.setFont(label8Font);
        label8.setText("Statistics");
        panel5.add(label8, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        statisticsTextArea = new JTextArea();
        statisticsTextArea.setEditable(false);
        statisticsTextArea.setText("Statistics");
        panel5.add(statisticsTextArea, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        childResultsInitializePanel.add(panel6, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        Font label9Font = this.$$$getFont$$$(null, Font.BOLD, 16, label9.getFont());
        if (label9Font != null) label9.setFont(label9Font);
        label9.setText("Last action");
        panel6.add(label9, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final com.intellij.uiDesigner.core.Spacer spacer7 = new com.intellij.uiDesigner.core.Spacer();
        panel6.add(spacer7, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        lastActionStatisticsTextArea = new JTextArea();
        lastActionStatisticsTextArea.setEditable(false);
        lastActionStatisticsTextArea.setText("None");
        panel6.add(lastActionStatisticsTextArea, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        childResultsInitializePanel.add(panel7, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        Font label10Font = this.$$$getFont$$$(null, Font.BOLD, 16, label10.getFont());
        if (label10Font != null) label10.setFont(label10Font);
        label10.setText("Last Book Borrowed");
        panel7.add(label10, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final com.intellij.uiDesigner.core.Spacer spacer8 = new com.intellij.uiDesigner.core.Spacer();
        panel7.add(spacer8, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        bookBorrowedStatisticsTextBox = new JTextArea();
        bookBorrowedStatisticsTextBox.setEditable(false);
        bookBorrowedStatisticsTextBox.setText("None");
        panel7.add(bookBorrowedStatisticsTextBox, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        childResultsInitializePanel.add(panel8, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        Font label11Font = this.$$$getFont$$$(null, Font.BOLD, 16, label11.getFont());
        if (label11Font != null) label11.setFont(label11Font);
        label11.setText("Last Book Returned");
        panel8.add(label11, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 1, false));
        final com.intellij.uiDesigner.core.Spacer spacer9 = new com.intellij.uiDesigner.core.Spacer();
        panel8.add(spacer9, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        bookReturnedStatisticsTextBox = new JTextArea();
        bookReturnedStatisticsTextBox.setEditable(false);
        bookReturnedStatisticsTextBox.setText("None");
        panel8.add(bookReturnedStatisticsTextBox, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        initializeControlsPanel = new JPanel();
        initializeControlsPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        initializeTabPanel.add(initializeControlsPanel, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        startSimulationButton = new JButton();
        startSimulationButton.setText("Start Simulation");
        initializeControlsPanel.add(startSimulationButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return managementPanel;
    }


}
