# How-Tos

## How to Migrate a Git Repository

**Step 1 – Mirror clone**

When you want to clone a repository for the purpose of migration, you really want everything, including all the other refs that are not branches:

- Git Tags (refs/tags/*)
- Git Notes (refs/notes/*)
- Gerrit Reviews (refs/changes/*)
- Gerrit Configs (refs/meta/*)

Instead of using a standard clone, you can do a “git clone –mirror”, which implies –bare and thus does not generate a working copy.

**Example:**

```bash
$ git clone --mirror ssh://myuser@gitent-scm.com/git/myorg/myrepo.git
Cloning into bare repository 'myrepo.git'...
remote: Counting objects: 109, done
remote: Finding sources: 100% (109/109)
remote: Total 109 (delta 19), reused 83 (delta 19)
Receiving objects: 100% (109/109), 66.42 KiB | 0 bytes/s, done.
Resolving deltas: 100% (19/19), done.
Checking connectivity... done.
```

**Step 2 – Create empty repo on the new Git Server**

You need to have an empty target repository where to push your mirrored local clone. Note that most of the Git Servers propose you to create a first master branch with a README, but, in this case, you do not need it and it would only create more trouble in your migration path.

**Step 3 – Push to the new Git Server**

You are now ready to push to the target repository, and we can use the useful option “–mirror” again.
Similarly to the clone, “–mirror” automatically include all refs, including the non-branch ones (tags, notes, reviews, configs, …); it provides the behaviour of removing all the refs that are not present in your local clone. You should never use this option when you have a “regular default clone” as you would risk removing all the remote refs that have not been typically cloned with a standard default “git clone” operation.

**Example for GitHub:**

```bash
$ git push --mirror git@github.myorg/myrepo.git
Counting objects: 109, done.
Delta compression using up to 4 threads.
Compressing objects: 100% (61/61), done.
Writing objects: 100% (109/109), 66.42 KiB | 0 bytes/s, done.
Total 109 (delta 19), reused 109 (delta 19)
To git@github.myorg/myrepo.git
* [new branch] refs/changes/02/802/1 -> refs/changes/02/802/1
* [new branch] refs/changes/03/803/1 -> refs/changes/03/803/1
* [new branch] master -> master
* [new branch] refs/meta/config -> refs/meta/config
```

# Troubleshooting

## failed to connect to github.com: Invalid argument

### Cause

A search in internet shows that the cullprit is certainly the firewall/ssh configuration of the company I work for. 

### Solution

I switched to https instead of ssh, and everything works fine.

# Authors

* **Su, Xiaobin** *- Author*

