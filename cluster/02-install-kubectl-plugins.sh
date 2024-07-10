#!/bin/bash

# Define the bold text format
bold=$(tput bold)
normal=$(tput sgr0)

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check if krew is installed
if ! command_exists kubectl-krew; then
    echo ""
    echo "${bold}Error: krew is not installed. Please install krew first.${normal}"
    echo ""
    exit 1
fi

# Install kubectl-tree plugin
echo ""
echo "${bold}Installing kubectl-tree plugin...${normal}"
echo ""
kubectl krew install tree
echo ""
echo "${bold}Verify by running 'kubectl tree --help'.${normal}"
echo ""

# Install cnpg kubectl plugin
echo ""
echo "${bold}Installing cnpg kubectl plugin...${normal}"
echo ""
kubectl krew install cnpg
echo ""
echo "${bold}Verify by running 'kubectl cnpg --help'.${normal}"
echo ""

# Install kubectl ctx plugin
echo ""
echo "${bold}Installing kubectl ctx plugin...${normal}"
echo ""
kubectl krew install ctx
echo ""
echo "${bold}Verify by running 'kubectl ctx --help'.${normal}"
echo ""

# Install kubectl ns plugin
echo ""
echo "${bold}Installing kubectl ns plugin...${normal}"
echo ""
kubectl krew install ns
echo ""
echo "${bold}Verify by running 'kubectl ns --help'.${normal}"
echo ""

# Install cert-manager kubectl plugin
echo ""
echo "${bold}Installing cert-manager kubectl plugin...${normal}"
echo ""
kubectl krew install cert-manager
echo ""
echo "${bold}Verify by running 'kubectl cert-manager --help'.${normal}"
echo ""

# List all installed krew plugins and their versions
echo ""
echo "${bold}Listing all installed krew plugins and their versions...${normal}"
echo ""
kubectl krew list
echo ""
