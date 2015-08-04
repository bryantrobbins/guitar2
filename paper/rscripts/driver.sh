#!/bin/bash

dataset="amalga_ArgoUML_sq_l_1_testCaseFeatures_n_0_data"
myGammaExp="0"
myCostExp="0"
accessKey="AKIAIZRZIIU5OBQ2F3DA"
secretKey="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"

cmd="Rscript tune.r ${dataset} ${myGammaExp} ${myCostExp} ${accessKey} ${secretKey} > tune.out 2>&1"
echo $cmd
eval $cmd
