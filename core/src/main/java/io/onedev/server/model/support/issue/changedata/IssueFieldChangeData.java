package io.onedev.server.model.support.issue.changedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Component;

import io.onedev.server.OneDev;
import io.onedev.server.manager.GroupManager;
import io.onedev.server.manager.UserManager;
import io.onedev.server.model.Group;
import io.onedev.server.model.IssueChange;
import io.onedev.server.model.User;
import io.onedev.server.util.CommentSupport;
import io.onedev.server.util.IssueField;
import io.onedev.server.util.inputspec.InputSpec;
import io.onedev.server.web.component.diff.plain.PlainDiffPanel;
import io.onedev.utils.StringUtils;

public class IssueFieldChangeData implements IssueChangeData {

	private static final long serialVersionUID = 1L;

	protected final Map<String, IssueField> oldFields;
	
	protected final Map<String, IssueField> newFields;
	
	public IssueFieldChangeData(Map<String, IssueField> oldFields, Map<String, IssueField> newFields) {
		this.oldFields = copyNonEmptyFields(oldFields);
		this.newFields = copyNonEmptyFields(newFields);
	}
	
	protected List<String> getOldLines() {
		List<String> oldLines = new ArrayList<>();
		for (IssueField oldField: oldFields.values()) {
			IssueField newField = newFields.get(oldField.getName());
			if (newField == null || !describe(oldField).equals(describe(newField)))
				oldLines.add(describe(oldField));
		}
		for (IssueField newField: newFields.values()) {
			if (!oldFields.containsKey(newField.getName()))
				oldLines.add("");
		}
		return oldLines;
	}
	
	protected List<String> getNewLines() {
		List<String> newLines = new ArrayList<>();
		for (IssueField oldField: oldFields.values()) {
			IssueField newField = newFields.get(oldField.getName());
			if (newField != null) {
				if (!describe(oldField).equals(describe(newField))) {
					newLines.add(describe(newField));
				}
			} else {
				newLines.add("");
			}
		}
		for (IssueField newField: newFields.values()) {
			if (!oldFields.containsKey(newField.getName()))
				newLines.add(describe(newField));
		}
		return newLines;
	}
	
	private Map<String, IssueField> copyNonEmptyFields(Map<String, IssueField> fields) {
		Map<String, IssueField> copy = new LinkedHashMap<>();
		for (Map.Entry<String, IssueField> entry: fields.entrySet()) {
			if (!entry.getValue().getValues().isEmpty())
				copy.put(entry.getKey(), entry.getValue());
		}
		return copy;
	}
	
	private String describe(IssueField field) {
		return field.getName() + ": " + StringUtils.join(field.getValues(), ", ");		
	}
	
	@Override
	public Component render(String componentId, IssueChange change) {
		return new PlainDiffPanel(componentId, getOldLines(), "a.txt", getNewLines(), "b.txt", true);
	}

	@Override
	public String getDescription() {
		return "changed fields";
	}

	public List<String> getLines(Map<String, IssueField> fields) {
		List<String> lines = new ArrayList<>();
		for (Map.Entry<String, IssueField> entry: fields.entrySet())
			lines.add(entry.getKey() + ": " + StringUtils.join(entry.getValue().getValues(), ", "));
		return lines;
	}
	
	@Override
	public CommentSupport getCommentSupport() {
		return null;
	}

	@Override
	public Map<String, User> getNewUsers() {
		UserManager userManager = OneDev.getInstance(UserManager.class);
		Map<String, User> newUsers = new HashMap<>();
		for (IssueField oldField: oldFields.values()) {
			IssueField newField = newFields.get(oldField.getName());
			if (newField != null && !describe(oldField).equals(describe(newField)) 
					&& newField.getType().equals(InputSpec.USER) && !newField.getValues().isEmpty()) { 
				User user = userManager.findByName(newField.getValues().iterator().next());
				if (user != null)
					newUsers.put(newField.getName(), user);
			}
		}
		for (IssueField newField: newFields.values()) {
			if (!oldFields.containsKey(newField.getName()) && newField.getType().equals(InputSpec.USER) 
					&& !newField.getValues().isEmpty()) { 
				User user = userManager.findByName(newField.getValues().iterator().next());
				if (user != null)
					newUsers.put(newField.getName(), user);
			}
		}
		return newUsers;
	}

	
	@Override
	public Map<String, Group> getNewGroups() {
		Map<String, Group> newGroups = new HashMap<>();
		GroupManager groupManager = OneDev.getInstance(GroupManager.class);
		for (IssueField oldField: oldFields.values()) {
			IssueField newField = newFields.get(oldField.getName());
			if (newField != null 
					&& !describe(oldField).equals(describe(newField)) 
					&& newField.getType().equals(InputSpec.GROUP) 
					&& !newField.getValues().isEmpty()) { 
				Group group = groupManager.find(newField.getValues().iterator().next());
				if (group != null)
					newGroups.put(newField.getName(), group);
			}
		}
		for (IssueField newField: newFields.values()) {
			if (!oldFields.containsKey(newField.getName()) 
					&& newField.getType().equals(InputSpec.GROUP) 
					&& !newField.getValues().isEmpty()) { 
				Group group = groupManager.find(newField.getValues().iterator().next());
				if (group != null)
					newGroups.put(newField.getName(), group);
			}
		}
		return newGroups;
	}
	
	@Override
	public boolean affectsBoards() {
		return true;
	}

}
