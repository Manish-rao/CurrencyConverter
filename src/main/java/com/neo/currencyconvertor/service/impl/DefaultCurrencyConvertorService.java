package com.neo.currencyconvertor.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.currencyconvertor.model.BestCurrency;
import com.neo.currencyconvertor.model.Currency;
import com.neo.currencyconvertor.service.CurrencyConvertorService;

@Service
public class DefaultCurrencyConvertorService implements CurrencyConvertorService {

	RestTemplate restTemplate = new RestTemplate();

	@Value("${neo.api.url}")
	String neoApiUrl;

	ObjectMapper mapper = new ObjectMapper();

	private BestCurrency getDataFromNeoApi(String source, String target)
			throws JsonMappingException, JsonProcessingException, InterruptedException {
		String jsonString = restTemplate.getForObject(neoApiUrl, String.class);
		// System.out.println(jsonString);
		List<Currency> currencyList = mapper.readValue(jsonString, new TypeReference<List<Currency>>() {
		});
		Map<String, List<Currency>> currencyNodes = currencyList.stream().collect(Collectors
				.groupingBy(Currency::getFromCurrencyCode, HashMap::new, Collectors.toCollection(ArrayList::new)));
		Node rootNode = new Node(source);
		rootNode.price = 100.0d;
		addChildNodes(currencyNodes, source, target, null, rootNode);
		Map<String, Double> output = printAllElements(rootNode, target, source);
		BestCurrency bestCurrency = null;
		output.forEach((k, v) -> {
			System.out.println("Key:" + k + "    Price:" + v);
		});
		if (output.isEmpty())
			return bestCurrency;
		bestCurrency = getBestCurrency(output, currencyNodes, source);
		return bestCurrency;
	}

	public BestCurrency getBestCurrency(Map<String, Double> output, Map<String, List<Currency>> currencyNodes,
			String source) {
		Entry<String, Double> entry = output.entrySet().iterator().next();
		String key = entry.getKey();
		Double value = entry.getValue();
		Currency sourceCurrency = currencyNodes.get(source).get(0);
		String[] countryArray = sourceCurrency.getFromCurrencyName().split("\\s");
		String country = "";
		if (country.length() > 0) {
			country = String.join(" ", Arrays.copyOf(countryArray, countryArray.length - 1));
		} else {
			country = countryArray[0];
		}
		BestCurrency bestCurrency = new BestCurrency(source, country, value, key);
		return bestCurrency;
	}

	public void addChildNodes(Map<String, List<Currency>> currencyNodes, String source, String target, Node parentNode,
			Node rootNode) {
		Map<String, List<Currency>> currencyNodesForCurrent = new HashMap<>();
		if (parentNode != null)
			currencyNodesForCurrent = currencyNodes.entrySet().stream().filter(x -> x.getKey().equals(parentNode.value))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		else
			currencyNodesForCurrent = currencyNodes.entrySet().stream().filter(x -> x.getKey().equals(rootNode.value))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		for (Entry<String, List<Currency>> entry : currencyNodesForCurrent.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(source)) {
				for (Currency curr : entry.getValue()) {
					// System.out.println("Target for current:" + curr.getToCurrencyCode());
					if (rootNode.visitedNodes
							.contains(new ImmutablePair<String, String>(entry.getKey(), curr.getToCurrencyCode()))) {
						// System.out.println("Breaking*******");
						break;
					}
					if (parentNode == null) {
						Node childNode = new Node(curr.getToCurrencyCode());
						Double price = rootNode.price;
						price = price - curr.getExchangeRate();
						childNode.price = price;

						if (curr.getToCurrencyCode().equals(target))
							childNode.shouldContinue = false;
						rootNode.visitedNodes.add(new ImmutablePair<String, String>(entry.getKey(), childNode.value));
						rootNode.addChild(childNode);
					} else {
						Node childNode = new Node(curr.getToCurrencyCode());
						Double price = parentNode.price;
						price = price - curr.getExchangeRate();
						childNode.price = price;
						if (curr.getToCurrencyCode().equals(target))
							childNode.shouldContinue = false;
						rootNode.visitedNodes.add(new ImmutablePair<String, String>(entry.getKey(), childNode.value));
						parentNode.addChild(childNode);
					}

				}
			}
		}
		Node nodeNew = null;
		if (parentNode == null) {
			nodeNew = rootNode;
		} else {
			nodeNew = parentNode;
		}

		for (Node childNode : nodeNew.children) {
			// System.out.println("Node in Loop: " + childNode.value + "Shoud continue?" +
			// childNode.shouldContinue);
			if (childNode.shouldContinue)
				addChildNodes(currencyNodes, childNode.value, target, childNode, rootNode);
			// else
			// break;
		}
	}

	@Override
	public BestCurrency convertSourceToTargetCurrency(String source, String target) {
		try {
			BestCurrency bestCurrency = getDataFromNeoApi(source, target);
			return bestCurrency;
		} catch (JsonProcessingException | InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, Double> printAllElements(Node node, String target, String source) {
		StringBuilder sb = new StringBuilder();
		Map<String, Double> listOfStrings = new HashMap<>();
		inOrderTraversal(node, sb, listOfStrings, target, source);
		Map<String, Double> sorted = listOfStrings.entrySet().stream()
				.sorted(Collections.reverseOrder(Map.Entry.<String, Double>comparingByValue()))
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
		return sorted;
	}

	private void inOrderTraversal(Node node, StringBuilder sb, Map<String, Double> listOfStrings, String target,
			String source) {
		if (node.children != null || node.children.size() > 0) {
			for (Node node1 : node.children) {
				// System.out.println("Node is " + node1.toString());
				inOrderTraversal(node1, sb, listOfStrings, target, source);
				if (node1.value.equalsIgnoreCase(target)) {
					Node node2 = node1;
					while (node2.parent != null) {
						sb.append(node2.value);
						sb.append("-");
						node2 = node2.parent;
					}
					sb.append(source);
					List<String> temp = Arrays.asList(sb.toString().split("-"));
					Collections.reverse(temp);
					String result = String.join("|", temp);
					// result = result + " : " + node1.price;
					listOfStrings.put(result, node1.price);
					sb = new StringBuilder();
				}
			}
		}
	}

	static class Node {
		List<Node> children;
		Node parent;
		String value;
		Double price;
		boolean shouldContinue;
		List<ImmutablePair<String, String>> visitedNodes = new LinkedList<ImmutablePair<String, String>>();

		public Node(String value) {
			this.value = value;
			children = new CopyOnWriteArrayList<>();
			shouldContinue = true;
		}

		public Node addChild(Node node) {
			children.add(node);
			node.parent = this;
			return this;
		}

		@Override
		public String toString() {
			return "Node value=" + value + "price" + price + "]";
		}

	}
}
