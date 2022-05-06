# How do you set a default branch in Git?

```bash
git config --global init.defaultBranch <desired_branch_name>
```

If the branch was already set to something else, you can rename/move branches as necessary with

```bash
git branch -m <old> <new>
```

As necessary, you can use whatever settings your remote provider gives you to do the same remotely.

[Home](/)
