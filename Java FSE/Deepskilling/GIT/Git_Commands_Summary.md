# Git Hands-On Labs Summary of Commands

This document summarizes the core Git command sequences for all 5 Git Hands-On Labs (HOL).

---

## Lab 1: Git Configuration & First File

### 1. Setup User Profile & Editor
Set global configurations for your username, email, and choose the default text editor (e.g. Notepad++ or Nano/Vim):
```bash
# Configure username and email
git config --global user.name "Arun Kumar"
git config --global user.email "arun@example.com"

# Set default text editor (example: Notepad++)
git config --global core.editor "'C:/Program Files/Notepad++/notepad++.exe' -multiInst -notabbar -nosession -noPlugin"

# Verify configuration list
git config --list
```

### 2. Initialize and Commit First File
Initialize a new local repository, stage a text file, and make the first commit:
```bash
# Initialize local Git repository
git init

# Check status
git status

# Stage the file
git add sample.txt

# Commit the staged file
git commit -m "First commit: added sample.txt"

# Add remote origin link
git remote add origin https://github.com/iamarun0708/java-fsd.git

# Push changes to main branch
git push -u origin main
```

---

## Lab 2: Branching and Merging

### 1. Create and Switch Branches
Manage feature branches to perform parallel development safely:
```bash
# Create feature branch 'development'
git branch development

# Switch to development branch
git checkout development

# Shortcut command: create and switch in one go
git checkout -b feature-login
```

### 2. Merge Branches
Merge completed features back into the main branch:
```bash
# Switch back to main branch
git checkout main

# Merge development changes into main
git merge development

# View commit log graph
git log --oneline --graph --all
```

---

## Lab 3: Stashing Changes

### Stash & Retrieve Uncommitted Work
Save uncommitted changes temporarily to keep a clean working directory, switch context, and restore them later:
```bash
# Stash tracked changes
git stash

# List stashed changes
git stash list

# Switch to hotfix branch, complete hotfix, merge, switch back, and pop stash
git stash pop
```

---

## Lab 4: Resolving Merge Conflicts

### Handle Conflicts During Merge
Identify and resolve overlapping edits in the same file from different branches:
```bash
# Attempt merge that results in conflict
git merge development
# Git outputs: CONFLICT (content): Merge conflict in file.txt

# 1. Open the conflicted file, locate conflict markers (<<<<<<<, =======, >>>>>>>)
# 2. Resolve the differences manually and save the file
# 3. Stage the resolved file
git add file.txt

# 4. Finalize the merge commit
git commit -m "Resolved merge conflict in file.txt"
```

---

## Lab 5: Rebase and History Rewriting

### 1. Rebasing Branches
Apply commits of a branch on top of another branch to keep a linear commit history:
```bash
# Switch to feature branch
git checkout feature-branch

# Rebase feature-branch on top of main
git rebase main
```

### 2. Undo/Reset Changes
Revert commits or unstage files safely:
```bash
# Unstage a file without losing modifications
git reset HEAD file.txt

# Soft reset (revert last commit, keep changes staged)
git reset --soft HEAD~1

# Hard reset (completely revert commit and discard all modifications - Caution!)
git reset --hard HEAD~1
```
