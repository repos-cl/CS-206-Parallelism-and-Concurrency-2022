# Tools Setup

## Note

We recommend using Linux or macOS for this course, we also support Windows but
typically people have more trouble getting everything working correctly on
Windows and it's harder for us to help them since we don't use Windows
ourselves.

**On Windows, if your username has spaces or special characters in it, the IDE might not
work properly. Please create a new user with a username containing only letters.**

## Step 1: Create an account on gitlab.epfl.ch

If you haven't already [log into gitlab](https://gitlab.epfl.ch/users/sign_in)
and fill in [this form](https://forms.gle/N6F3Q3jZm71AASby9) with your GASPAR and SCIPER number to initialize your GitLab repository for the course. Do this as soon as possible because it will take some time between the account creation and the lab submission system working for your account.

## Step 2: Installing the Java Development Kit (JDK) and sbt via coursier

We will use coursier to install the correct version of
Java as well as the sbt build tool. Follow [the instructions on the coursier website](https://get-coursier.io/docs/cli-installation) for the installation.

_Note_: on Windows, you might have to run the installer twice.

## Step 3: Installing git

git is a version control system.

### On Ubuntu and Debian

```shell
sudo apt update && sudo apt install git
```

### On macOS

First, install the Homebrew package manager if it is not already installed:

```shell
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

Use Homebrew to install git:

```shell
brew install git
```

### On Windows

Download and install git from [https://git-scm.com/downloads](https://git-scm.com/downloads).
Once git is installed, open a **new** terminal and run:

```shell
git config --global core.autocrlf false
```

If this command worked it will not print anything.

## Step 4: Installing Code

Visual Studio Code is the IDE we strongly recommend using for this class (you are free to use any editor you want, but we won't don't have the resources to help you configure it for Scala).

### On Linux

See [https://code.visualstudio.com/docs/setup/linux](https://code.visualstudio.com/docs/setup/linux)

### On macOS

If you don't already have Code installed:
```shell
brew install --cask visual-studio-code
```
If it was already installed before, you will need to [enable launching it from the
command line](https://code.visualstudio.com/docs/setup/mac#_launching-from-the-command-line).

### On Windows

See [https://code.visualstudio.com/docs/setup/windows](https://code.visualstudio.com/docs/setup/windows).
Make sure that the checkbox "Add to PATH (available after restart)" in the
installer is checked.

## Step 5: Installing the Scala support for Code

Open a **new** terminal and run:
```scala
code --install-extension lampepfl.dotty-syntax
```

If you're on Windows and the command is not found, try closing and restarting
the terminal, if that doesn't work

## Step 6: Generate a public/private SSH key pair

To submit labs, you will need an SSH key. If you don't already have one, here's how to generate it:

### Step 6.1: Installing OpenSSH

#### On Ubuntu and Debian

```shell
sudo apt update && sudo apt install openssh-client
```

#### On macOS

Nothing to do, OpenSSH is pre-installed

#### On Windows

Follow the instructions under "Enable OpenSSH Client in Windows 10" on
[https://winaero.com/blog/enable-openssh-client-windows-10/](https://winaero.com/blog/enable-openssh-client-windows-10/)

### Step 6.2: Generating the key pair

Please follow [this tutorial](https://docs.github.com/en/authentication/connecting-to-github-with-ssh/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent) to generate a new SSH key and add it to the ssh-agent (except the last step: you should instead add the key to Gitlab as described below). This [video](https://youtu.be/_RsP81Et12s?t=67) (from 1:07 to 4:05) might also help. 

### Step 6.3: Adding your public key on Gitlab

To be able to push your code, you'll need to add the public part of your key on Gitlab:
- Go to [gitlab.epfl.ch](https://gitlab.epfl.ch), log in with your EPFL account
- Go to [gitlab.epfl.ch/-/profile/keys](https://gitlab.epfl.ch/-/profile/keys) and copy-paste the content of the `id_rsa.pub` file created by the `ssh-keygen` command you just ran (`ssh-keygen` prints the location of the file to the console).
- Press `Add key`

## Step 7: Follow the example lab

Time to do the [example lab](example-lab.md)!
