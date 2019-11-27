#!/bin/bash

# 批量删除远程分支 shell script

reponsitory_name="$1"
cd $reponsitory_name;
echo " -R welcome the 【$reponsitory_name】 reponsitory, you will delete the branches, WARNNING !!! ";
echo " -R !!! WARNNING !!! ";
all_branches=`git branch -r`;
#echo $all_branches;
read -p " -W Are you sure delete the branch ** $br1 (y/n)[n]: " answer
for br1 in $all_branches;
do
    br1_simple_name=`echo $br1 | grep '/' | cut -d '/' -f3`
    if [[ "" !=  "$br1_simple_name"
            && "HEAD" != "$br1_simple_name" 
            && "master" != "$br1_simple_name" 
            ]]; then
        echo " -D begin delete branch " $br1 " --->> " $br1_simple_name;
        
        if [[ "$answer" = "Y" || "${answer}" = "y"  ]]; then
            echo " Yes! deleting branch $br1 -> $br1_simple_name";
            git push origin --delete origin/$br1_simple_name;
        else
            echo " Skipped!!!";
        fi;
    fi;
    
done;
echo "over";