# QUB Museum Manager

This is my Java project for managing a museum. It runs in the console. You can add artifacts, put them into exhibits, and then plan which exhibits go on show each year.

The main code is in `StarterCode/src/part01/QUBMuseum.java`.

## What it does

- **Artifacts:** add, view, update and delete them
- **Exhibits:** add, view, update and delete them, and add or remove artifacts in them
- **Annual plans:** make a plan for a year and add exhibits to it, with a hall and a month

## Files

- `QUBMuseum.java` is the main class with the menu and all the logic
- `Artifact.java`, `Exhibit.java` and `AnnualPlan.java` store the data
- `MenuStates.java` lists the different menu screens
- `TestingQUBMuseum.java` has the tests

## How it works

Everything is stored in HashMaps, and each item gets an ID number (0, 1, 2...). Nothing is saved when you close the program.

The menu uses a state variable to remember which screen you're on. When you type something, the program decides what to do based on that state.

## How to run it

1. Open the `StarterCode` folder in your IDE
2. Run the `main` method in `QUBMuseum.java`
3. Type a number from the menu and press Enter

Some options ask for several things at once, split up with `;` and no spaces. For example, to add an artifact:

```
type;name;description;engagementMinutes
```

## Things that aren't finished

- Some of the input validation methods are empty
- A few methods will crash if you give them an ID that doesn't exist
- There are some small bugs in searching and sorting that I still need to fix
