function gdataset = simulateoracledata(graph, varargin)
% function gdataset = simulateoracledata(graph, varargin)
% creates pseudodataset gdataset to be used in oracle versions of all
% algorithms with 'msep' test of independence
% Author: striant@csd.uoc.gr
% =======================================================================
% Inputs
% =======================================================================
% graph                   = nVars x nVars DAG adjacency matrix
% varargin                = optional arguments isLatent, isManipulated
% =======================================================================
% Outputs
% =======================================================================
% dataset                  = struct describing the data, 
%    .data                 = the semi-Markov causal model of the dag after
%                            marginalizing out the variables isLatent
%    .isAncestor           = nVars x nVars ancestor matrix: 
%                           isAncestor(i,j) =1 if i is an ancestor of j
%                           in graph, 0 otherwise
%    .isLatent            = nVars x 1 boolean vector, true for latent
%                           variables (default false for all)
%    .isManipulated       = nVars x 1 boolean vector, true for manipulated
%                           variables (default false for all)
% =======================================================================
numNodes = length(graph);
[isLatent, isManipulated] = process_options(varargin, 'isLatent',  false(1, numNodes),'isManipulated', false(1, numNodes));
gdataset.data = dag2smm(graph, isLatent);
gdataset.isAncestor = transitiveClosureSparse_mex(sparse(graph));

gdataset.isLatent = isLatent;
gdataset.isManipulated = isManipulated;
gdataset.type = 'oracle';

end