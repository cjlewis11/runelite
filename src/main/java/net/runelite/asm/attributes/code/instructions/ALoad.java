package net.runelite.asm.attributes.code.instructions;

import net.runelite.asm.attributes.code.Instruction;
import net.runelite.asm.attributes.code.InstructionType;
import net.runelite.asm.attributes.code.Instructions;
import net.runelite.asm.attributes.code.instruction.types.LVTInstruction;
import net.runelite.asm.attributes.code.instruction.types.WideInstruction;
import net.runelite.asm.execution.Frame;
import net.runelite.asm.execution.InstructionContext;
import net.runelite.asm.execution.Stack;
import net.runelite.asm.execution.StackContext;
import net.runelite.asm.execution.VariableContext;
import net.runelite.asm.execution.Variables;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ALoad extends Instruction implements LVTInstruction, WideInstruction
{
	private int index;
	private boolean wide;
	
	public ALoad(Instructions instructions, int index)
	{
		super(instructions, InstructionType.ALOAD, 0);
		this.index = index;
		++length;
	}

	public ALoad(Instructions instructions, InstructionType type, int pc)
	{
		super(instructions, type, pc);
	}
	
	public ALoad(Instructions instructions, InstructionType type, Instruction instruction, int pc)
	{
		super(instructions, type, pc);
		wide = true;
	}
	
	@Override
	public void load(DataInputStream is) throws IOException
	{
		if (wide)
		{
			index = is.readShort();
			length += 2;
		}
		else
		{
			index = is.readByte();
			length += 1;
		}
	}
	
	@Override
	public void write(DataOutputStream out) throws IOException
	{
		super.write(out);
		out.writeByte(index);
	}

	@Override
	public InstructionContext execute(Frame frame)
	{
		InstructionContext ins = new InstructionContext(this, frame);
		Stack stack = frame.getStack();
		Variables var = frame.getVariables();
		
		VariableContext vctx = var.get(index);
		ins.read(vctx);
		
		StackContext ctx = new StackContext(ins, vctx);
		stack.push(ctx);
		
		ins.push(ctx);
		
		return ins;
	}

	@Override
	public int getVariableIndex()
	{
		return index;
	}

	@Override
	public boolean store()
	{
		return false;
	}
	
	@Override
	public void writeWide(DataOutputStream out) throws IOException
	{
		super.write(out);
		out.writeShort(index);
	}

	@Override
	public Instruction setVariableIndex(int idx)
	{
		index = idx;
		return this;
	}

	@Override
	public Instruction makeSpecific()
	{
		switch (index)
		{
			case 0:
				return new ALoad_0(this.getInstructions());
			case 1:
				return new ALoad_1(this.getInstructions());
			case 2:
				return new ALoad_2(this.getInstructions());
			case 3:
				return new ALoad_3(this.getInstructions());
			default:
				return this;
		}
	}
}
