######################################################################
# python helper to access some parts of GINsim API through jython
# import this file for use in your own scripts
######################################################################
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph as rg

# for the simulation
from fr.univmrs.tagc.GINsim.reg2dyn import Simulation
from fr.univmrs.tagc.GINsim.reg2dyn import GsBatchSimulationFrame
from fr.univmrs.tagc.GINsim.reg2dyn import DynamicalHierarchicalSimulation
from fr.univmrs.tagc.GINsim.reg2dyn import SimulationManager
from fr.univmrs.tagc.GINsim.reg2dyn import GsSimulationParametersManager
from fr.univmrs.tagc.GINsim.regulatoryGraph.initialState import GsInitialState
from fr.univmrs.tagc.GINsim.connectivity import AlgoConnectivity
from fr.univmrs.tagc.GINsim.animator import GsRegulatoryAnimator

# initial states
from fr.univmrs.tagc.GINsim.regulatoryGraph.initialState import GsInitialStateManager

# mutants
from fr.univmrs.tagc.GINsim.regulatoryGraph import GsMutantListManager

# stable states
from fr.univmrs.tagc.GINsim.stableStates import GsSearchStableStates

# for the reduction
from fr.univmrs.tagc.GINsim.regulatoryGraph.modelModifier import ModelSimplifier
from fr.univmrs.tagc.GINsim.regulatoryGraph.modelModifier import ModelSimplifierPlugin
# for reports
from fr.univmrs.tagc.common.document import OOoDocumentWriter, WikiDocumentWriter, XHTMLDocumentWriter, LaTeXDocumentWriter
from fr.univmrs.tagc.GINsim.export.generic import GsSVGExport

import fr.univmrs.tagc.GINsim.graph.GsGinsimGraphDescriptor as GD
from java.io import File
from java.util import HashMap

# load GINsim core, otherwise nothing works!
from fr.univmrs.tagc.GINsim.global import GsMain
from fr.univmrs.tagc.common import Tools

class GINsim:
	def __init__(self):
		pass

	def open(self, path):
		"open a model from a path"
		return GD.getInstance().open(File(path))

	def export_SVG(self, graph, filename):
		"export the graph as SVG"
		GsSVGExport.exportSVG(graph, False, filename)

	def reduce(self, graph, reduction):
		"reduce an opened model"
		# get the reduction def
		config = graph.getObject("modelSimplifier", True).getElement(reduction)
		simplifier = ModelSimplifier(graph, config, None, False)
		return simplifier.do_reduction()

	def get_scc_graph(self, graph):
		algo = AlgoConnectivity()
		algo.configure(graph,None, AlgoConnectivity.MODE_FULL)
		return algo.compute()

	def get_simulations(self, graph, param=None):
		"get all simulation parameters"
		simulations = graph.getObject(GsSimulationParametersManager.key, True)
		if simulations and param:
		    return simulations.getElement(param)
		return simulations.v_data

	def get_initialStates(self, graph, param=None):
		"get all defined initial states"
		ilist = graph.getObject(GsInitialStateManager.key, True).getInitialStates()
		if ilist and param:
		    return ilist.getElement(param)
		return ilist.v_data

	def get_inputs(self, graph, param=None):
		"get all defined input configurations"
		inputs = graph.getObject(GsInitialStateManager.key, True).getInputConfigs()
		if inputs and param:
		    return inputs.getElement(param)
		return inputs.v_data

	def get_mutants(self, graph, param=None):
		"get all mutants or a mutant by name"
		mutants = graph.getObject(GsMutantListManager.key, True)
		if param:
		    return mutants.getElement(param)
		return mutants.v_data

	def get_stable_states(self, graph, mutant=None):
		stable_algo = GsSearchStableStates(graph, mutant, None)
		return stable_algo.getStable()

	def get_states_from_mdd(self, nbNodes, mdd):
		path = [ -1 for i in xrange(nbNodes) ]
		states = []
		browse_mdd(path, mdd, states)
		return states

	def browse_mdd(self, path, mdd, states):
		if mdd.next is None:
		    if mdd.value > 0:
		        states.append(path[:])
		else:
		    for i in xrange(len(mdd.next)):
		        path[mdd.level] = i
		        browse_mdd(path, mdd.next[i], states)
		    path[mdd.level] = -1

	def create_report(self, path, properties={}, js=None):
		"create a report file, this creates the file and DocumentWriter, don't forget to close it"
		if path.endswith("html"):
		    dw = XHTMLDocumentWriter()
		elif path.endswith("odt"):
		    dw = OOoDocumentWriter()
		elif path.endswith("tex"):
		    dw = LaTeXDocumentWriter()
		else:
		    dw = WikiDocumentWriter()
		dw.setOutput(File(path))
		for prop, value in properties.items():
		    dw.setDocumentProperty(prop, value)
		if js:
		    dw.getDocumentExtra("javascript").append(js)
		dw.startDocument()
		return dw

	def name_state(self, state, graph):
		"get the name of a state (according to initial state names)"
		name = GsBatchSimulationFrame.nameState(state, graph)
		return name

	def get_graph_coloriser(self, graph):
		"get a graph coloriser: colorise acording to states. Don't forget to call endColorization() when you are done"
		return GsRegulatoryAnimator(graph).colorizer


class SimulationReporter(SimulationManager):
    "helper to perform simulations"
    def __init__(self, graph, stable_handler=None):
        self.graph = graph
        self.nodeOrder = graph.getNodeOrder()
        self.stable_handler = stable_handler

    def endSimu(self, graph):
        print graph

    def setProgress(self, n):
        pass

    def addStableState(self, item):
        if self.stable_handler is not None:
            self.stable_handler(item)

    def run(self, simulation, init=None, inputs=None):
        if simulation.__class__ == "".__class__:
            simulation = get_simulations(self.graph, simulation)
        sim = Simulation(self.graph, self, simulation, False, True)

        if init or inputs:
            m_init, m_input = self.get_mstate(init), self.get_mstate(inputs)
            sim.set_initialStates(self.nodeOrder, m_input, m_init);
        return sim.do_simulation()

    def get_mstate(self, state):
        try:
            istate = GsInitialState()
            istate.setState(state, self.nodeOrder)
        except:
            istate = state
        m_state = HashMap()
        m_state[istate] = None
        return m_state

